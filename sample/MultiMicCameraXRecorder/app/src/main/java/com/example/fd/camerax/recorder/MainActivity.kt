package com.example.fd.camerax.recorder

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fd.camerax.recorder.compose.MainScreen
import com.example.fd.camerax.recorder.ui.theme.MultiMicCameraXRecorderTheme

/**
 * このアプリは，THINKLET向けのCameraXを用いた録画サンプルアプリです．
 * 第２ボタン（Cameraキー）の押下により，録画と録画の停止を行います．
 * 書き出し先ファイルは， `/sdcard/Android/data/com.example.fd.camerax.recorder/files/` 以下にmp4形式で保存されます．
 */
class MainActivity : ComponentActivity() {
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.addFlags(FLAG_KEEP_SCREEN_ON)
        viewModel.setup(this, this.lifecycle)
        setContent {
            MultiMicCameraXRecorderTheme {
                MainScreen(
                    isRecording = viewModel.isRecording,
                    isLandscape = viewModel.isLandscape(this),
                    buildPreview = {
                        viewModel.buildRecorder(
                            context = this, lifecycleOwner = this, preview = it
                        )
                    })
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_CAMERA -> {
                viewModel.toggleRecordState(this)
                return true
            }

            else -> false
        }
    }
}
