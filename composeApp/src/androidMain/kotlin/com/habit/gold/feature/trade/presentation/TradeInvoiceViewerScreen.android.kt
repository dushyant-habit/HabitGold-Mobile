package com.habit.gold.feature.trade.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_back
import habitgoldmobile.composeapp.generated.resources.common_retry
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_download_invoice
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_invalid_url
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_file_name
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_invoice_saved
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_open_external
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_page_description
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_title
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_unable_to_prepare_invoice
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_unable_to_render_invoice
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_unable_to_save_invoice
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_unable_to_load_invoice
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource

private val InvoicePrimary = Color(0xFF7B2CBF)
private val InvoiceMutedText = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun TradeInvoiceViewerScreen(
    invoiceUrl: String,
    onBackClick: () -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    val invalidUrlMessage = stringResource(Res.string.trade_invoice_viewer_invalid_url)
    val openExternalLabel = stringResource(Res.string.trade_invoice_viewer_open_external)
    val invoiceSavedMessage = stringResource(Res.string.trade_invoice_viewer_invoice_saved)
    val unableToPrepareMessage = stringResource(Res.string.trade_invoice_viewer_unable_to_prepare_invoice)
    val unableToSaveMessage = stringResource(Res.string.trade_invoice_viewer_unable_to_save_invoice)
    val unableToLoadMessage = stringResource(Res.string.trade_invoice_viewer_unable_to_load_invoice)
    val unableToRenderMessage = stringResource(Res.string.trade_invoice_viewer_unable_to_render_invoice)
    val invoiceFileName = stringResource(Res.string.trade_invoice_viewer_file_name)
    val isValidUrl = invoiceUrl.startsWith("http://") || invoiceUrl.startsWith("https://")

    var pdfFile by remember(invoiceUrl) { mutableStateOf<File?>(null) }
    var isLoading by remember(invoiceUrl) { mutableStateOf(false) }
    var errorMessage by remember(invoiceUrl) { mutableStateOf<String?>(null) }
    var pendingExportFile by remember { mutableStateOf<File?>(null) }
    var pendingExportFileName by remember(invoiceFileName) { mutableStateOf(invoiceFileName) }

    val saveInvoiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
    ) { destinationUri ->
        val file = pendingExportFile
        pendingExportFile = null
        if (destinationUri == null) return@rememberLauncherForActivityResult
        if (file == null) {
            Toast.makeText(context, unableToSaveMessage, Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }

        coroutineScope.launch {
            runCatching {
                copyPdfToUri(context, file, destinationUri, unableToSaveMessage)
            }.onSuccess {
                Toast.makeText(context, invoiceSavedMessage, Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(context, unableToSaveMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun fetchPdf() {
        if (!isValidUrl) {
            errorMessage = invalidUrlMessage
            pdfFile = null
            isLoading = false
            return
        }
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            runCatching {
                downloadPdfToCache(
                    context = context,
                    invoiceUrl = invoiceUrl,
                    loadErrorMessage = unableToLoadMessage,
                )
            }.onSuccess { file ->
                pdfFile = file
            }.onFailure { error ->
                pdfFile = null
                errorMessage = error.message ?: unableToLoadMessage
            }
            isLoading = false
        }
    }

    LaunchedEffect(invoiceUrl) {
        fetchPdf()
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    Text(stringResource(Res.string.trade_invoice_viewer_title))
                },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            if (isValidUrl) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            val file = pdfFile ?: runCatching {
                                downloadPdfToCache(
                                    context = context,
                                    invoiceUrl = invoiceUrl,
                                    loadErrorMessage = unableToLoadMessage,
                                )
                            }.getOrNull()
                            if (file == null) {
                                Toast.makeText(context, unableToPrepareMessage, Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                            pdfFile = file
                            pendingExportFile = file
                            pendingExportFileName = invoiceFileName
                            saveInvoiceLauncher.launch(pendingExportFileName)
                        }
                    },
                    containerColor = InvoicePrimary,
                    contentColor = Color.White,
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = stringResource(Res.string.trade_invoice_viewer_download_invoice),
                    )
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage != null -> {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = errorMessage ?: unableToLoadMessage,
                            color = InvoiceMutedText,
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { fetchPdf() }) {
                            Text(stringResource(Res.string.common_retry))
                        }
                        if (isValidUrl) {
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { uriHandler.openUri(invoiceUrl) }) {
                                Text(openExternalLabel)
                            }
                        }
                    }
                }
                pdfFile != null -> {
                    AndroidPdfRendererViewer(
                        file = pdfFile!!,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun AndroidPdfRendererViewer(
    file: File,
    modifier: Modifier = Modifier,
) {
    val unableToRenderMessage = stringResource(Res.string.trade_invoice_viewer_unable_to_render_invoice)
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val targetWidthPx = remember(configuration.screenWidthDp, density) {
        with(density) { configuration.screenWidthDp.dp.roundToPx() * INVOICE_RENDER_SCALE }
    }
    val renderState by produceState<PdfRenderState>(
        initialValue = PdfRenderState.Loading,
        key1 = file.absolutePath,
        key2 = targetWidthPx,
    ) {
        value = runCatching {
            PdfRenderState.Success(renderPdfPages(file, targetWidthPx))
        }.getOrElse { error ->
            PdfRenderState.Error(error.message ?: "")
        }
    }

    val renderedPages = (renderState as? PdfRenderState.Success)?.pages
    DisposableEffect(renderedPages) {
        onDispose {
            renderedPages?.forEach(Bitmap::recycle)
        }
    }

    when (val state = renderState) {
        PdfRenderState.Loading -> {
            Box(modifier = modifier) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        is PdfRenderState.Error -> {
            Box(modifier = modifier.padding(24.dp)) {
                Text(
                    text = state.message.ifBlank { unableToRenderMessage },
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
        is PdfRenderState.Success -> {
            Box(
                modifier = modifier
                    .background(Color.White)
                    .clipToBounds(),
                contentAlignment = Alignment.TopCenter,
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    itemsIndexed(state.pages) { index, bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = stringResource(Res.string.trade_invoice_viewer_page_description, index + 1),
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth,
                        )
                    }
                }
            }
        }
    }
}

private sealed interface PdfRenderState {
    data object Loading : PdfRenderState
    data class Success(val pages: List<Bitmap>) : PdfRenderState
    data class Error(val message: String) : PdfRenderState
}

private suspend fun downloadPdfToCache(
    context: Context,
    invoiceUrl: String,
    loadErrorMessage: String,
): File = withContext(Dispatchers.IO) {
    val connection = (URL(invoiceUrl).openConnection() as HttpURLConnection).apply {
        connectTimeout = 15000
        readTimeout = 15000
        requestMethod = "GET"
        doInput = true
    }
    connection.connect()
    if (connection.responseCode !in 200..299) {
        connection.disconnect()
        error(loadErrorMessage)
    }
    val file = File.createTempFile("invoice_", ".pdf", context.cacheDir)
    connection.inputStream.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    connection.disconnect()
    file
}

private suspend fun copyPdfToUri(
    context: Context,
    sourceFile: File,
    destinationUri: Uri,
    saveErrorMessage: String,
) = withContext(Dispatchers.IO) {
    context.contentResolver.openOutputStream(destinationUri)?.use { output ->
        sourceFile.inputStream().use { input ->
            input.copyTo(output)
        }
    } ?: error(saveErrorMessage)
}

private fun renderPdfPages(file: File, targetWidthPx: Int): List<Bitmap> {
    val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    val renderer = PdfRenderer(fileDescriptor)
    val pages = buildList {
        for (index in 0 until renderer.pageCount) {
            val page = renderer.openPage(index)
            try {
                val width = targetWidthPx.coerceAtLeast(page.width)
                val scale = width.toFloat() / page.width.toFloat()
                val height = (page.height * scale).roundToInt().coerceAtLeast(1)
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bitmap)
                canvas.drawColor(Color.White.toArgb())
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                add(bitmap)
            } finally {
                page.close()
            }
        }
    }
    renderer.close()
    fileDescriptor.close()
    return pages
}

private const val INVOICE_RENDER_SCALE = 2
