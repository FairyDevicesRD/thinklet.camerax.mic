package com.example.fd.camerax.recorder.camerax

import ai.fd.thinklet.camerax.ThinkletAudioRecordWrapperFactory
import ai.fd.thinklet.camerax.ThinkletAudioSettingsPatcher
import ai.fd.thinklet.camerax.ThinkletMic
import ai.fd.thinklet.camerax.mic.ThinkletMics
import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import android.util.Range
import androidx.camera.video.audio.wrapper.AudioRecordWrapper
import androidx.camera.video.audio.wrapper.DefaultAudioRecordWrapper
import com.konovalov.vad.silero.VadSilero
import com.konovalov.vad.silero.config.FrameSize
import com.konovalov.vad.silero.config.Mode
import com.konovalov.vad.silero.config.SampleRate
import java.nio.ByteBuffer

fun ThinkletMics.catMic(context: Context): ThinkletMic = CatMic(context)

internal class CatMic(private val context: Context) : ThinkletMic {
    override fun getAudioSettingsPatcher(): ThinkletAudioSettingsPatcher? =
        CatAudioSettingsPatcher()

    override fun getAudioRecordWrapperFactory(): ThinkletAudioRecordWrapperFactory? =
        CatAudioRecordWrapperFactory(context)

    internal class CatAudioSettingsPatcher : ThinkletAudioSettingsPatcher {
        override fun getAudioSource(defaultSetting: Int): Int = MediaRecorder.AudioSource.MIC

        override fun getAudioFormat(defaultSetting: Int): Int = AudioFormat.ENCODING_PCM_16BIT

        override fun getChannelCount(defaultSetting: Int): Int = 1

        override fun getSampleRate(audioSpecRange: Range<Int>, defaultSampleRate: Int): Int = 16000
    }

    @SuppressLint("MissingPermission")
    internal class CatAudioRecordWrapperFactory(private val context: Context) : ThinkletAudioRecordWrapperFactory {
        private companion object {
            const val BUFFER_SIZE = 2048
        }

        val vad = VadSilero(
            context,
            sampleRate = SampleRate.SAMPLE_RATE_16K,
            frameSize = FrameSize.FRAME_SIZE_1024,
            mode = Mode.NORMAL,
            silenceDurationMs = 300,
            speechDurationMs = 50
        )

        override fun create(
            audioSource: Int,
            audioFormat: Int,
            channelCount: Int,
            sampleRate: Int
        ): AudioRecordWrapper {
            val channelMask = AudioFormat.CHANNEL_IN_MONO
            // create audioRecord.
            val audioRecord = AudioRecord.Builder()
                .setAudioSource(audioSource)
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(audioFormat)
                        .setSampleRate(sampleRate)
                        .setChannelMask(channelMask)
                        .build()
                )
                .setBufferSizeInBytes(BUFFER_SIZE)
                .build()

            return object : DefaultAudioRecordWrapper(audioRecord) {
                override fun read(byteBuffer: ByteBuffer, bufferSize: Int): Int {
                    val readSize = super.read(byteBuffer, BUFFER_SIZE)
                    if (readSize > 0) {
                        val audioData = ByteArray(readSize)
                        byteBuffer.rewind()
                        byteBuffer.get(audioData)
                        if (vad.isSpeech(audioData)) {
                            Log.i("MMM", "isSpeech")
                        } else {
                            byteBuffer.rewind()
                            byteBuffer.put(ByteArray(readSize))
                        }
                    }
                    return readSize
                }
            }
        }
    }
}
