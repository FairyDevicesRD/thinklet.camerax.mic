package ai.fd.thinklet.camerax.mic.xfe

import ai.fd.thinklet.camerax.ThinkletAudioRecordWrapperFactory
import ai.fd.thinklet.camerax.ThinkletAudioSettingsPatcher
import ai.fd.thinklet.camerax.ThinkletMic
import ai.fd.thinklet.camerax.mic.ThinkletMics

/**
 * An implementation of [ThinkletMic] using XFE feature.
 */
internal object XfeThinkletMic : ThinkletMic {

    override fun getAudioSettingsPatcher(): ThinkletAudioSettingsPatcher = XfeAudioSettingsPatcher()

    override fun getAudioRecordWrapperFactory(): ThinkletAudioRecordWrapperFactory? = null
}

/**
 * A mic mode for using a XFE feature.
 *
 * To use this mic mode, the caller must configure to enable the XFE in advance.
 */
val ThinkletMics.Xfe: ThinkletMic
    get() = XfeThinkletMic
