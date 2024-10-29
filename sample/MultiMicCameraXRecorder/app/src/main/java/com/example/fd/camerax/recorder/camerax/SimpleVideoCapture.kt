package com.example.fd.camerax.recorder.camerax

import ai.fd.thinklet.camerax.ThinkletMic
import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.video.ExperimentalPersistentRecording
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.util.Consumer
import com.example.fd.camerax.recorder.util.Logging
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal class SimpleVideoCapture(
    private val context: Context,
    private val listener: Consumer<VideoRecordEvent>,
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
) {
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    fun get(mic: ThinkletMic?): VideoCapture<Recorder> {
        videoCapture = VideoCapture.withOutput(
            Recorder.Builder()
                .setExecutor(executor)
                .setQualitySelector(QualitySelector.from(Quality.FHD))
                .apply { mic?.also { setThinkletMic(it) } }
                .build()
        )
        return videoCapture!!
    }

    fun isRecording(): Boolean = (recording != null)

    @OptIn(ExperimentalPersistentRecording::class)
    @SuppressLint("MissingPermission")
    fun startRecording(
        outputFile: File,
    ) {
        if (recording != null) {
            Logging.w("already recording")
            return
        }
        Logging.d("write to ${outputFile.absolutePath}")
        recording = videoCapture?.output
            ?.prepareRecording(
                context,
                FileOutputOptions
                    .Builder(outputFile)
                    .setFileSizeLimit(FILE_SIZE)
                    .build()
            )
            ?.withAudioEnabled()
            ?.start(executor, listener)
    }

    fun stopRecording() {
        if (recording == null) {
            Logging.w("already stopped")
            return
        }
        recording?.stop()
        recording = null
    }

    private companion object {
        const val FILE_SIZE = 4L * 1000 * 1000 * 1000
    }
}
