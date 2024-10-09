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
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.fd.camerax.recorder.camerax.CameraXPatch
import com.example.fd.camerax.recorder.camerax.SimpleVideoCapture
import com.example.fd.camerax.recorder.camerax.SimpleVideoCaptureImpl
import com.example.fd.camerax.recorder.camerax.ThinkletRecorder
import com.example.fd.camerax.recorder.camerax.ThinkletRecorderImpl
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface IMainViewModel {
    /**
     * 録画中の状態．
     */
    val isRecording: State<Boolean>

    /**
     * カメラが横向きかどうかを判定する
     */
    fun isLandscape(context: Context): Boolean

    /**
     * 必要なオブジェクトの生成やライフサイクルへの紐づけを行います．
     */
    fun setup(context: Context, lifecycle: Lifecycle)

    /**
     * レコーダーを生成します．引数のPreviewを与えることで，画面上にPreviewを表示します．
     */
    fun buildRecorder(context: Context, lifecycleOwner: LifecycleOwner, preview: Preview?)

    /**
     * 録画 <-> 停止 を切り替えます
     */
    fun toggleRecordState(context: Context)
}

class MainViewModel : ViewModel(), IMainViewModel, DefaultLifecycleObserver {
    init {
        // THINKLET向けの起動高速化パッチを適応します．
        CameraXPatch.apply()
    }

    private val recorder: ThinkletRecorder = ThinkletRecorderImpl()
    private lateinit var capture: SimpleVideoCapture
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

    override val isRecording: State<Boolean> = _isRecording

    @MainThread
    override fun setup(context: Context, lifecycle: Lifecycle) {
        lifecycle.addObserver(this)

        capture = SimpleVideoCaptureImpl(context, listener)
        // マイクの設定を更新します．ここでは，XFEを使うように設定します．
        mic = ThinkletMics.Xfe(context.getSystemService(AudioManager::class.java))
    }

    override fun buildRecorder(
        context: Context, lifecycleOwner: LifecycleOwner, preview: Preview?
    ) {
        recorder.build(
            context = context,
            lifecycleOwner = lifecycleOwner,
            preview = preview,
            videoCapture = capture.get(mic)
        )
    }

    override fun toggleRecordState(context: Context) {
        if (capture.isRecording()) {
            capture.stopRecording()
        } else {
            val file = context.getFile()
            Toast.makeText(context, "StartRecord: ${file.absoluteFile}", Toast.LENGTH_LONG).show()
            capture.startRecording(file)
        }
    }

    override fun isLandscape(context: Context): Boolean {
        val cameraManager = context.getSystemService(CameraManager::class.java)
        val cid = cameraManager.cameraIdList[0]
        val characteristics = cameraManager.getCameraCharacteristics(cid)
        val angle = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 90
        return arrayOf(0, 180).contains(angle)
    }


    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        sound = MediaActionSound().apply {
            load(MediaActionSound.START_VIDEO_RECORDING)
            load(MediaActionSound.STOP_VIDEO_RECORDING)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        sound?.release()
    }

    private fun Context.getFile(): File {
        return File(
            this.getExternalFilesDir(null),
            "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.JAPAN).format(Date())}.mp4"
        )
    }
}
