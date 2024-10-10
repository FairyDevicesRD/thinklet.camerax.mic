package ai.fd.thinklet.camerax.mic.xfe

import ai.fd.thinklet.camerax.ThinkletAudioSettingsPatcher
import android.media.AudioFormat
import android.media.MediaRecorder
import android.util.Range

/**
 * An implementation of [ThinkletAudioSettingsPatcher] for using XFE.
 */
class XfeAudioSettingsPatcher : ThinkletAudioSettingsPatcher {

    override fun getAudioSource(defaultSetting: Int): Int = MediaRecorder.AudioSource.MIC

    override fun getAudioFormat(defaultSetting: Int): Int = AudioFormat.ENCODING_PCM_16BIT

    override fun getChannelCount(defaultSetting: Int): Int = 1

    override fun getSampleRate(audioSpecRange: Range<Int>, defaultSampleRate: Int): Int = when {
        audioSpecRange.contains(48000) -> 48000
        audioSpecRange.contains(16000) -> 16000
        else -> throw IllegalStateException(
            "Specified sample rate $audioSpecRange isn't supported with XFE. " +
                    "Specify 16000 or 48000."
        )
    }
}
