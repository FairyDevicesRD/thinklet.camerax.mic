package com.example.fd.camerax.recorder

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
import com.example.fd.camerax.recorder.camerax.SimpleVideoCapture
import com.example.fd.camerax.recorder.camerax.SimpleVideoCaptureImpl
import com.example.fd.camerax.recorder.camerax.ThinkletRecorder
import com.example.fd.camerax.recorder.camerax.ThinkletRecorderImpl
import com.example.fd.camerax.recorder.compose.CameraPreview
import com.example.fd.camerax.recorder.ui.theme.MultiMicCameraXRecorderTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity(), Consumer<VideoRecordEvent> {
    private val recorder: ThinkletRecorder = ThinkletRecorderImpl()
    private var capture: SimpleVideoCapture? = null
    private var sound: MediaActionSound? = null

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

    @Composable
    fun MainScreen() {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            // TODO: check permission
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
                    videoCapture = capture?.get() //TODO
                )
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
