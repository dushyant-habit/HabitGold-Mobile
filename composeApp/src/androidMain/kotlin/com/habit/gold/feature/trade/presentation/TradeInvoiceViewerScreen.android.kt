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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_back
import habitgoldmobile.composeapp.generated.resources.common_retry
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_download_invoice
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_invalid_url
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_file_name
import habitgoldmobile.composeapp.generated.resources.trade_invoice_viewer_invoice_saved
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
import java.security.MessageDigest
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    val coroutineScope = rememberCoroutineScope()
    val invalidUrlMessage = stringResource(Res.string.trade_invoice_viewer_invalid_url)
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
    val documentState by produceState<PdfDocumentState>(
        initialValue = PdfDocumentState.Loading,
        key1 = file.absolutePath,
    ) {
        value = runCatching {
            PdfDocumentState.Success(openPdfDocument(file))
        }.getOrElse { error ->
            PdfDocumentState.Error(error.message ?: "")
        }
    }
    val renderedPageCache = remember(file.absolutePath, targetWidthPx) {
        mutableStateMapOf<Int, Bitmap>()
    }
    val document = (documentState as? PdfDocumentState.Success)?.document
    DisposableEffect(document, renderedPageCache) {
        onDispose {
            document?.close()
            renderedPageCache.values.forEach(Bitmap::recycle)
            renderedPageCache.clear()
        }
    }

    when (val state = documentState) {
        PdfDocumentState.Loading -> {
            Box(modifier = modifier) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        is PdfDocumentState.Error -> {
            Box(modifier = modifier.padding(24.dp)) {
                Text(
                    text = state.message.ifBlank { unableToRenderMessage },
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
        is PdfDocumentState.Success -> {
            var zoomScale by remember(file.absolutePath) { mutableFloatStateOf(1f) }
            var contentOffsetX by remember(file.absolutePath) { mutableFloatStateOf(0f) }
            var contentOffsetY by remember(file.absolutePath) { mutableFloatStateOf(0f) }
            val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
                val nextScale = (zoomScale * zoomChange).coerceIn(1f, MAX_INVOICE_ZOOM_SCALE)
                if (nextScale <= 1f) {
                    zoomScale = 1f
                    contentOffsetX = 0f
                    contentOffsetY = 0f
                } else {
                    zoomScale = nextScale
                    contentOffsetX += panChange.x
                    contentOffsetY += panChange.y
                }
            }
            Box(
                modifier = modifier
                    .background(Color.White)
                    .clipToBounds(),
                contentAlignment = Alignment.TopCenter,
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .transformable(state = transformableState)
                        .pointerInput(file.absolutePath) {
                            detectTapGestures(
                                onDoubleTap = {
                                    val shouldReset = zoomScale > 1f
                                    zoomScale = if (shouldReset) 1f else DEFAULT_DOUBLE_TAP_ZOOM_SCALE
                                    contentOffsetX = 0f
                                    contentOffsetY = 0f
                                }
                            )
                        }
                        .graphicsLayer {
                            scaleX = zoomScale
                            scaleY = zoomScale
                            translationX = contentOffsetX
                            translationY = contentOffsetY
                            transformOrigin = TransformOrigin(0f, 0f)
                        },
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(
                        items = List(state.document.pageCount) { it },
                        key = { it },
                    ) { pageIndex ->
                        AndroidPdfPage(
                            pageIndex = pageIndex,
                            document = state.document,
                            renderedPageCache = renderedPageCache,
                            targetWidthPx = targetWidthPx,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AndroidPdfPage(
    pageIndex: Int,
    document: PdfDocumentHandle,
    renderedPageCache: SnapshotStateMap<Int, Bitmap>,
    targetWidthPx: Int,
    modifier: Modifier = Modifier,
) {
    val pageDescription = stringResource(Res.string.trade_invoice_viewer_page_description, pageIndex + 1)
    val cachedBitmap = renderedPageCache[pageIndex]
    val pageBitmap by produceState<Bitmap?>(
        initialValue = cachedBitmap,
        key1 = document.filePath,
        key2 = pageIndex,
        key3 = targetWidthPx,
    ) {
        val existing = renderedPageCache[pageIndex]
        if (existing != null && !existing.isRecycled) {
            value = existing
            return@produceState
        }
        val renderedPage = runCatching {
            document.renderPage(pageIndex = pageIndex, targetWidthPx = targetWidthPx)
        }.getOrNull()
        if (renderedPage != null) {
            renderedPageCache[pageIndex] = renderedPage
        }
        value = renderedPage
    }

    if (pageBitmap == null) {
        Box(
            modifier = modifier
                .height(INVOICE_PAGE_PLACEHOLDER_HEIGHT)
                .background(Color(0xFFF8FAFC)),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    } else {
        Image(
            bitmap = pageBitmap!!.asImageBitmap(),
            contentDescription = pageDescription,
            modifier = modifier,
            contentScale = ContentScale.FillWidth,
        )
    }
}

private sealed interface PdfDocumentState {
    data object Loading : PdfDocumentState
    data class Success(val document: PdfDocumentHandle) : PdfDocumentState
    data class Error(val message: String) : PdfDocumentState
}

private class PdfDocumentHandle(
    val filePath: String,
    private val fileDescriptor: ParcelFileDescriptor,
    private val renderer: PdfRenderer,
) {
    val pageCount: Int = renderer.pageCount
    private val renderMutex = Mutex()

    suspend fun renderPage(pageIndex: Int, targetWidthPx: Int): Bitmap = withContext(Dispatchers.IO) {
        renderMutex.withLock {
            renderer.openPage(pageIndex).usePage { page ->
                val width = targetWidthPx.coerceAtLeast(page.width)
                val scale = width.toFloat() / page.width.toFloat()
                val height = (page.height * scale).roundToInt().coerceAtLeast(1)
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bitmap ->
                    val canvas = android.graphics.Canvas(bitmap)
                    canvas.drawColor(Color.White.toArgb())
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                }
            }
        }
    }

    fun close() {
        renderer.close()
        fileDescriptor.close()
    }
}

private suspend fun downloadPdfToCache(
    context: Context,
    invoiceUrl: String,
    loadErrorMessage: String,
): File = withContext(Dispatchers.IO) {
    val cachedFile = File(context.cacheDir, "invoice_${invoiceUrl.stableCacheKey()}.pdf")
    if (cachedFile.exists() && cachedFile.length() > 0L) {
        return@withContext cachedFile
    }
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
    val tempFile = File.createTempFile("invoice_", ".pdf", context.cacheDir)
    connection.inputStream.use { input ->
        FileOutputStream(tempFile).use { output ->
            input.copyTo(output)
        }
    }
    connection.disconnect()
    if (!tempFile.renameTo(cachedFile)) {
        tempFile.copyTo(cachedFile, overwrite = true)
        tempFile.delete()
    }
    cachedFile
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

private fun openPdfDocument(file: File): PdfDocumentHandle {
    val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    val renderer = PdfRenderer(fileDescriptor)
    return PdfDocumentHandle(
        filePath = file.absolutePath,
        fileDescriptor = fileDescriptor,
        renderer = renderer,
    )
}

private const val INVOICE_RENDER_SCALE = 2
private const val MAX_INVOICE_ZOOM_SCALE = 4f
private const val DEFAULT_DOUBLE_TAP_ZOOM_SCALE = 2f
private val INVOICE_PAGE_PLACEHOLDER_HEIGHT = 320.dp

private inline fun <T> PdfRenderer.Page.usePage(block: (PdfRenderer.Page) -> T): T {
    return try {
        block(this)
    } finally {
        close()
    }
}

private fun String.stableCacheKey(): String {
    return MessageDigest.getInstance("SHA-256")
        .digest(toByteArray())
        .joinToString(separator = "") { byte -> "%02x".format(byte) }
        .take(24)
}
