package com.example.fd.camerax.vadrecorder.camerax.mics

import ai.fd.thinklet.camerax.ThinkletAudioRecordWrapperFactory
import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import androidx.camera.video.audio.wrapper.AudioRecordWrapper
import androidx.camera.video.audio.wrapper.DefaultAudioRecordWrapper
import java.nio.ByteBuffer

@SuppressLint("MissingPermission")
internal class VadAudioRecordWrapperFactory(private val isSpeech: (ByteArray) -> Boolean) :
    ThinkletAudioRecordWrapperFactory {
    private companion object {
        const val BUFFER_SIZE = 2048
    }

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
                    if (!isSpeech(audioData)) {
                        // clear audio.
                        byteBuffer.rewind()
                        byteBuffer.put(ByteArray(readSize))
                    }
                }
                return readSize
            }
        }
    }
}
