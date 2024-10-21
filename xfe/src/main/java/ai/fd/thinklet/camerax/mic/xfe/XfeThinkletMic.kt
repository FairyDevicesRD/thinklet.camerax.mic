package ai.fd.thinklet.camerax.mic.xfe

import ai.fd.thinklet.camerax.ThinkletAudioRecordWrapperFactory
import ai.fd.thinklet.camerax.ThinkletAudioSettingsPatcher
import ai.fd.thinklet.camerax.ThinkletMic
import ai.fd.thinklet.camerax.mic.ThinkletMics
import ai.fd.thinklet.sdk.audio.ExperimentalXfeFeature
import android.media.AudioManager

/**
 * An implementation of [ThinkletMic] using XFE feature.
 */
@OptIn(ExperimentalXfeFeature::class)
internal class XfeThinkletMic(private val audioManager: AudioManager) : ThinkletMic {

    override fun getAudioSettingsPatcher(): ThinkletAudioSettingsPatcher = XfeAudioSettingsPatcher()
    override fun getAudioRecordWrapperFactory(): ThinkletAudioRecordWrapperFactory? =
        XfeAudioRecordWrapperFactory.get(audioManager)
}

/**
 * A mic mode for recording using XFE + 5ch mics.
 *
 * The number of channels output is mono(1ch) only.
 * @param audioManager AudioManager
 */
fun ThinkletMics.Xfe(audioManager: AudioManager): ThinkletMic = XfeThinkletMic(audioManager)
