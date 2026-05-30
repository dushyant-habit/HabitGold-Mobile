package com.habit.gold.feature.trade.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import com.habit.gold.core.di.startKoinIfNeeded
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_download_invoice
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_file_name
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_invalid_url
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_title
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.interpretObjCPointerOrNull
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import platform.CoreFoundation.CFDataCreate
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Foundation.NSUUID
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.darwin.NSObject

@Composable
@OptIn(ExperimentalForeignApi::class)
actual fun TradeInvoiceViewerScreen(
    invoiceUrl: String,
    onBackClick: () -> Unit,
    modifier: Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val httpClient = remember { startKoinIfNeeded().get<HttpClient>() }
    val isValidUrl = invoiceUrl.startsWith("http://") || invoiceUrl.startsWith("https://")
    val downloadInvoiceLabel = stringResource(Res.string.trade_invoice_viewer_download_invoice)
    val invoiceFileName = stringResource(Res.string.trade_invoice_viewer_file_name)

    TradeChildScaffold(
        title = stringResource(Res.string.trade_invoice_viewer_title),
        onBackClick = onBackClick,
        bottomBar = {
            if (isValidUrl) {
                var isDownloading by remember(invoiceUrl) { mutableStateOf(false) }
                Box(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Button(
                        onClick = {
                            if (isDownloading) return@Button
                            coroutineScope.launch {
                                isDownloading = true
                                val fileUrl = runCatching {
                                    downloadInvoiceToTemporaryFile(
                                        httpClient = httpClient,
                                        invoiceUrl = invoiceUrl,
                                        fallbackFileName = invoiceFileName,
                                    )
                                }.getOrNull()
                                isDownloading = false
                                if (fileUrl != null) {
                                    presentShareSheet(fileUrl)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (isDownloading) {
                            CircularProgressIndicator()
                        } else {
                            Text(downloadInvoiceLabel)
                        }
                    }
                }
            }
        },
    ) { paddingValues ->
        if (!isValidUrl) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Text(stringResource(Res.string.trade_invoice_viewer_invalid_url))
            }
            return@TradeChildScaffold
        }

        var isLoading by remember(invoiceUrl) { mutableStateOf(true) }
        val delegate = remember(invoiceUrl) {
            object : NSObject(), WKNavigationDelegateProtocol {
                override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
                    isLoading = false
                }
            }
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            UIKitView(
                factory = {
                    WKWebView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0), configuration = WKWebViewConfiguration()).apply {
                        navigationDelegate = delegate
                        loadRequest(NSURLRequest.requestWithURL(NSURL(string = invoiceUrl)))
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { webView ->
                    webView.navigationDelegate = delegate
                },
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private suspend fun downloadInvoiceToTemporaryFile(
    httpClient: HttpClient,
    invoiceUrl: String,
    fallbackFileName: String,
): NSURL = withContext(Dispatchers.Default) {
    val data = httpClient.get(invoiceUrl).bodyAsBytes().toNSData()
    val fileName = "${NSUUID().UUIDString}_${fallbackFileName}"
    val targetPath = NSTemporaryDirectory() + fileName
    if (!NSFileManager.defaultManager.createFileAtPath(targetPath, data, null)) {
        error("Unable to save invoice")
    }
    NSURL.fileURLWithPath(targetPath)
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData = usePinned { pinned ->
    val cfData = CFDataCreate(
        allocator = null,
        bytes = pinned.addressOf(0).reinterpret(),
        length = size.toLong(),
    )
    interpretObjCPointerOrNull<NSData>(cfData!!.rawValue)
        ?: error("Unable to prepare invoice file data")
}

@OptIn(ExperimentalForeignApi::class)
private fun presentShareSheet(fileUrl: NSURL) {
    val presenter = topViewController() ?: return
    val activityController = UIActivityViewController(
        activityItems = listOf(fileUrl),
        applicationActivities = null,
    )
    presenter.presentViewController(activityController, animated = true, completion = null)
}

private fun topViewController(): UIViewController? {
    val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return null
    var top = root
    while (top.presentedViewController != null) {
        top = top.presentedViewController!!
    }
    return top
}
