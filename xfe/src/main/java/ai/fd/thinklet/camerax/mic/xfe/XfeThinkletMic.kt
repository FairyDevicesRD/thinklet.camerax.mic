package ai.fd.thinklet.camerax.mic.xfe

import ai.fd.thinklet.camerax.ThinkletAudioRecordWrapperFactory
import ai.fd.thinklet.camerax.ThinkletAudioSettingsPatcher
import ai.fd.thinklet.camerax.ThinkletMic
import ai.fd.thinklet.camerax.mic.ThinkletMics
import ai.fd.thinklet.sdk.audio.ExperimentalXfeFeature
import ai.fd.thinklet.sdk.audio.configureXfe
import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import androidx.camera.core.Logger
import androidx.camera.video.audio.wrapper.DefaultAudioRecordWrapper

/**
 * An implementation of [ThinkletMic] using XFE feature.
 */
@OptIn(ExperimentalXfeFeature::class)
internal class XfeThinkletMic(private val audioManager: AudioManager) : ThinkletMic {
    private companion object {
        const val TAG = "XfeThinkletMic"
    }

    override fun getAudioSettingsPatcher(): ThinkletAudioSettingsPatcher = XfeAudioSettingsPatcher()

    @SuppressLint("MissingPermission", "RestrictedApi")
    override fun getAudioRecordWrapperFactory(): ThinkletAudioRecordWrapperFactory? {
        if (!audioManager.configureXfe(true)) {
            Logger.e(TAG, "Failed to configureXfe")
            return null
        }

        Logger.d(TAG, "getAudioRecordWrapperFactory")
        return ThinkletAudioRecordWrapperFactory { audioSource, audioFormat, channelCount, sampleRate ->
            // replace channelCount(1 or 2) to CHANNEL_IN_MONO or CHANNEL_IN_STEREO
            val channelMask =
                if (channelCount == 1) AudioFormat.CHANNEL_IN_MONO else AudioFormat.CHANNEL_IN_STEREO
            // get minimum buffer size.
            val bufSize = AudioRecord.getMinBufferSize(sampleRate, channelMask, audioFormat)
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
                .setBufferSizeInBytes(bufSize)
                .build()
            object : DefaultAudioRecordWrapper(audioRecord) {
                override fun release() {
                    super.release()
                    audioManager.configureXfe(false)
                    Logger.d(TAG, "called configureXfe(false)")
                }
            }
        }
    }
}

/**
 * A mic mode for recording using XFE + 5ch mics.
 *
 * The number of channels output is mono(1ch) only.
 * @param audioManager AudioManager
 */
fun ThinkletMics.Xfe(audioManager: AudioManager): ThinkletMic = XfeThinkletMic(audioManager)
