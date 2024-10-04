package com.example.fd.camerax.recorder

import ai.fd.thinklet.camerax.ThinkletMic
import android.Manifest
import android.media.AudioManager
import android.media.MediaActionSound
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.video.VideoRecordEvent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.util.Consumer
import com.example.fd.camerax.recorder.camerax.CameraXPatch
import com.example.fd.camerax.recorder.camerax.SimpleVideoCapture
import com.example.fd.camerax.recorder.camerax.SimpleVideoCaptureImpl
import com.example.fd.camerax.recorder.camerax.ThinkletRecorder
import com.example.fd.camerax.recorder.camerax.ThinkletRecorderImpl
import com.example.fd.camerax.recorder.camerax.mics.ThinkletMicSelector
import com.example.fd.camerax.recorder.compose.CameraPreview
import com.example.fd.camerax.recorder.ui.theme.MultiMicCameraXRecorderTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity(), Consumer<VideoRecordEvent> {
    private val recorder: ThinkletRecorder = ThinkletRecorderImpl()
    private var capture: SimpleVideoCapture? = null
    private var sound: MediaActionSound? = null
    private val mic: ThinkletMic? by lazy {
        ThinkletMicSelector.xfe(this.getSystemService(AudioManager::class.java))
        // ThinkletMicSelector.simpleFive(isStereo = true)
        // ThinkletMicSelector.droid(isStereo = true)
    }

    init {
        CameraXPatch.apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiMicCameraXRecorderTheme {
                MainScreen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sound?.release()
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun MainScreen() {
        val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
        val micPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            if (!cameraPermissionState.status.isGranted) {
                cameraPermissionState.launchPermissionRequest()
            } else if (!micPermissionState.status.isGranted) {
                micPermissionState.launchPermissionRequest()
            } else {
                CameraPreview(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) { preview ->
                    capture = SimpleVideoCaptureImpl(
                        context = this,
                        listener = this
                    )
                    sound = MediaActionSound().apply {
                        this.load(MediaActionSound.START_VIDEO_RECORDING)
                        this.load(MediaActionSound.STOP_VIDEO_RECORDING)
                    }
                    recorder.build(
                        context = this,
                        lifecycleOwner = this,
                        preview = preview,
                        videoCapture = capture?.get(mic)
                    )
                }
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_CAMERA -> {
                if (capture == null) return true
                if (capture?.isRecording() == true) {
                    capture?.stopRecording()
                } else {
                    val file = getFile()
                    Toast.makeText(this, "StartRecord: ${file.absoluteFile}", Toast.LENGTH_LONG)
                        .show()
                    capture?.startRecording(file)
                }
                return true
            }

            else -> false
        }
    }

    override fun accept(value: VideoRecordEvent) {
        when (value) {
            is VideoRecordEvent.Start -> sound?.play(MediaActionSound.START_VIDEO_RECORDING)
            is VideoRecordEvent.Finalize -> sound?.play(MediaActionSound.STOP_VIDEO_RECORDING)
        }
    }

    private fun getFile(): File = File(
        this.getExternalFilesDir(null), "${
            SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
        }.mp4"
    )
}
