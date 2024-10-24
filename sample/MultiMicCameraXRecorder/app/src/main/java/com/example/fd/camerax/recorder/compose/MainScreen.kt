package com.example.fd.camerax.recorder.compose

import android.Manifest
import androidx.camera.core.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

/**
 * カメラのPreviewを表示．録画中は右上に緑色の丸図形を描画する Compose．
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    isRecording: State<Boolean>,
    isLandscape: Boolean,
    buildPreview: (preview: Preview) -> Unit,
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val micPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    Scaffold(modifier = modifier) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (!cameraPermissionState.status.isGranted) {
                cameraPermissionState.launchPermissionRequest()
            } else if (!micPermissionState.status.isGranted) {
                micPermissionState.launchPermissionRequest()
            } else {
                CameraPreview(
                    modifier = Modifier.fillMaxWidth(),
                    isLandscape = isLandscape,
                    renderPreview = { buildPreview(it) }
                )
                if (isRecording.value) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color.Green, shape = CircleShape)
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}
