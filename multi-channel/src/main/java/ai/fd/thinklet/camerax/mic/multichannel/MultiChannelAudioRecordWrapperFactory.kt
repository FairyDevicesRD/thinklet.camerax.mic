package ai.fd.thinklet.camerax.mic.multichannel

import ai.fd.thinklet.camerax.ThinkletAudioRecordWrapperFactory
import ai.fd.thinklet.sdk.audio.MultiChannelAudioRecord
import ai.fd.thinklet.sdk.audio.MultiChannelAudioRecord.Channel
import ai.fd.thinklet.sdk.audio.MultiChannelAudioRecord.SampleRate
import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.camera.video.audio.wrapper.AudioRecordWrapper

/**
 * An implementation of [ThinkletAudioRecordWrapperFactory] using [MultiChannelAudioRecord].
 */
internal class MultiChannelAudioRecordWrapperFactory(
    private val sourceChannelCount: Channel
) : ThinkletAudioRecordWrapperFactory {

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun create(
        audioSource: Int, audioFormat: Int, channelCount: Int,
        sampleRate: Int
    ): AudioRecordWrapper {
        val audioRecordWithBufferSize = MultiChannelAudioRecord().get(
            sourceChannelCount,
            getMultiChannelAudioRecordSampleRate(sampleRate)
        )
        return MultiChannelAudioRecordWrapper(
            audioRecordWithBufferSize.audioRecord,
            sourceChannelCount,
            audioRecordWithBufferSize.bufferSize,
            channelCount == 2
        )
    }

    private fun getMultiChannelAudioRecordSampleRate(sampleRate: Int): SampleRate =
        when (sampleRate) {
            16000 -> SampleRate.SAMPLING_RATE_16000
            32000 -> SampleRate.SAMPLING_RATE_32000
            48000 -> SampleRate.SAMPLING_RATE_48000
            else -> throw IllegalArgumentException("Unsupported sample rate. $sampleRate")
        }
}
