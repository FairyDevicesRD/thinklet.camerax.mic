package ai.fd.thinklet.camerax.mic.multichannel

import ai.fd.thinklet.camerax.ThinkletAudioRecordWrapperFactory
import ai.fd.thinklet.camerax.ThinkletAudioSettingsPatcher
import ai.fd.thinklet.camerax.ThinkletMic
import ai.fd.thinklet.camerax.mic.ThinkletMics
import ai.fd.thinklet.sdk.audio.MultiChannelAudioRecord

/**
 * An implementation of [ThinkletMic] using multi channel mic.
 */
internal class MultiChannelThinkletMic(
    private val sourceChannelCount: MultiChannelAudioRecord.Channel
) : ThinkletMic {

    override fun getAudioSettingsPatcher(): ThinkletAudioSettingsPatcher? = null

    override fun getAudioRecordWrapperFactory(): ThinkletAudioRecordWrapperFactory =
        MultiChannelAudioRecordWrapperFactory(sourceChannelCount)
}

/**
 * A mic mode for recording using 5 channel mics.
 * The number of channels output follows the CameraX settings.
 */
val ThinkletMics.FiveCh: ThinkletMic
    get() = MultiChannelThinkletMic(MultiChannelAudioRecord.Channel.CHANNEL_FIVE)

/**
 * A mic mode for recording using 6 channel mics.
 * The number of channels output follows the CameraX settings.
 */
val ThinkletMics.SixCh: ThinkletMic
    get() = MultiChannelThinkletMic(MultiChannelAudioRecord.Channel.CHANNEL_SIX)
