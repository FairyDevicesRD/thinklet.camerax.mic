package com.example.fd.camerax.vadrecorder.camerax.mics

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fd.camerax.vadrecorder.util.Logging
import com.konovalov.vad.silero.VadSilero
import com.konovalov.vad.silero.config.FrameSize
import com.konovalov.vad.silero.config.Mode
import com.konovalov.vad.silero.config.SampleRate
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch

/**
 * Voice Active Detection
 * @param context Context
 * @param lifecycleOwner LifecycleOwner
 */
internal class Vad(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {
    private var engine: VadSilero? = null

    init {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                Logging.d("create VAD")
                engine = VadSilero(
                    context,
                    sampleRate = SampleRate.SAMPLE_RATE_16K,
                    frameSize = FrameSize.FRAME_SIZE_1024,
                    mode = Mode.NORMAL,
                    silenceDurationMs = 300,
                    speechDurationMs = 50
                )
                try {
                    awaitCancellation()
                } finally {
                    engine?.close()
                    Logging.d("close VAD")
                }
            }
        }
    }

    fun isSpeech(byteArray: ByteArray): Boolean {
        return if (engine?.isSpeech(byteArray) == true) {
            Logging.d("isSpeech")
            true
        } else {
            false
        }
    }
}
