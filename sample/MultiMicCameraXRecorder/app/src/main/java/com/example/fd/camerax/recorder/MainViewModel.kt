package com.example.fd.camerax.recorder

import ai.fd.thinklet.camerax.ThinkletMic
import ai.fd.thinklet.camerax.mic.ThinkletMics
import ai.fd.thinklet.camerax.mic.xfe.Xfe
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.MediaActionSound
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.camera.core.Preview
import androidx.camera.video.VideoRecordEvent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.fd.camerax.recorder.camerax.CameraXPatch
import com.example.fd.camerax.recorder.camerax.SimpleVideoCapture
import com.example.fd.camerax.recorder.camerax.ThinkletRecorder
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel : ViewModel() {
    init {
        // THINKLET向けの起動高速化パッチを適応します．
        CameraXPatch.apply()
    }

    private val recorder: ThinkletRecorder = ThinkletRecorder()
    private var capture: SimpleVideoCapture? = null
    private var mic: ThinkletMic? = null

    private var sound: MediaActionSound? = null
    private val _isRecording = mutableStateOf(false)

    private val listener: Consumer<VideoRecordEvent>
        get() = Consumer { value ->
            when (value) {
                is VideoRecordEvent.Start -> {
                    sound?.play(MediaActionSound.START_VIDEO_RECORDING)
                    _isRecording.value = true
                }

                is VideoRecordEvent.Finalize -> {
                    sound?.play(MediaActionSound.STOP_VIDEO_RECORDING)
                    _isRecording.value = false
                }
            }
        }

    /**
     * 録画中の状態．
     */
    val isRecording: State<Boolean> = _isRecording

    /**
     * 必要なオブジェクトの生成を行います．
     */
    @MainThread
    fun setup(context: Context) {
        if (capture == null) capture = SimpleVideoCapture(context, listener)
        // マイクの設定を更新します．ここでは，XFEを使うように設定します．
        if (mic == null) mic = ThinkletMics.Xfe(context.getSystemService(AudioManager::class.java))
        sound = MediaActionSound().apply {
            load(MediaActionSound.START_VIDEO_RECORDING)
            load(MediaActionSound.STOP_VIDEO_RECORDING)
        }
    }

    /**
     * 不要なオブジェクトの削除を行います
     */
    @MainThread
    fun teardown() {
        sound?.release()
    }

    /**
     * レコーダーを生成します．引数のPreviewを与えることで，画面上にPreviewを表示します．
     */
    fun buildRecorder(
        context: Context, lifecycleOwner: LifecycleOwner, preview: Preview?
    ) {
        recorder.build(
            context = context,
            lifecycleOwner = lifecycleOwner,
            preview = preview,
            videoCapture = capture?.get(mic)
        )
    }

    /**
     * 録画 <-> 停止 を切り替えます
     */
    fun toggleRecordState(context: Context) {
        if (capture?.isRecording() == true) {
            capture?.stopRecording()
        } else {
            val file = context.getFile()
            Toast.makeText(context, "StartRecord: ${file.absoluteFile}", Toast.LENGTH_LONG).show()
            capture?.startRecording(file)
        }
    }

    /**
     * カメラが横向きかどうかを判定する
     */
    fun isLandscape(context: Context): Boolean {
        val cameraManager = context.getSystemService(CameraManager::class.java)
        val cid = cameraManager.cameraIdList[0]
        val characteristics = cameraManager.getCameraCharacteristics(cid)
        val angle = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 90
        return arrayOf(0, 180).contains(angle)
    }

    private fun Context.getFile(): File {
        return File(
            this.getExternalFilesDir(null),
            "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.JAPAN).format(Date())}.mp4"
        )
    }
}
