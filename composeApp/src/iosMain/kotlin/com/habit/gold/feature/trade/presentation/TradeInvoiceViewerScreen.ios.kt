package com.habit.gold.feature.trade.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_invalid_url
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_open_external
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_title
import kotlinx.cinterop.ExperimentalForeignApi
import org.jetbrains.compose.resources.stringResource
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.CoreGraphics.CGRectMake
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.darwin.NSObject

@Composable
@OptIn(ExperimentalForeignApi::class)
actual fun TradeInvoiceViewerScreen(
    invoiceUrl: String,
    onBackClick: () -> Unit,
    modifier: Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val isValidUrl = invoiceUrl.startsWith("http://") || invoiceUrl.startsWith("https://")

    TradeChildScaffold(
        title = stringResource(Res.string.trade_invoice_viewer_title),
        onBackClick = onBackClick,
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

            Button(
                onClick = { uriHandler.openUri(invoiceUrl) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
            ) {
                Text(stringResource(Res.string.trade_invoice_viewer_open_external))
            }
        }
    }
}
