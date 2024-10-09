package com.example.fd.camerax.recorder.compose

import android.util.Size
import android.view.ViewGroup
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraPreview(
    modifier: Modifier,
    isLandscape: Boolean,
    renderPreview: (Preview) -> Unit,
    previewSize: Size = Size(1920, 1080)
) {
    val width = if (isLandscape) previewSize.width else previewSize.height
    val height = if (isLandscape) previewSize.height else previewSize.width

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = PreviewView.ScaleType.FIT_CENTER
                this.layoutParams = ViewGroup.LayoutParams(width, height)
            }
            val preview = Preview.Builder()
                .build()
                .also { it.surfaceProvider = previewView.surfaceProvider }
            renderPreview(preview)
            return@AndroidView previewView
        }
    )
}
