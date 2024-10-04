package com.example.fd.camerax.recorder.camerax.mics

import ai.fd.thinklet.camerax.ThinkletAudioRecordWrapperFactory
import ai.fd.thinklet.camerax.ThinkletAudioSettingsPatcher
import ai.fd.thinklet.camerax.ThinkletMic
import ai.fd.thinklet.camerax.mic.ThinkletMics
import ai.fd.thinklet.camerax.mic.multichannel.FiveCh
import ai.fd.thinklet.camerax.mic.xfe.Xfe
import android.media.AudioManager
import android.util.Range

object ThinkletMicSelector {

    /**
     * Thinkletのシンプルな5chマイクを ステレオorモノラル録音する． [isStereo]
     */
    fun simpleFive(isStereo: Boolean = true): ThinkletMic = object : ThinkletMic {
        override fun getAudioSettingsPatcher(): ThinkletAudioSettingsPatcher =
            customPatcher(isStereo)

        override fun getAudioRecordWrapperFactory(): ThinkletAudioRecordWrapperFactory? =
            ThinkletMics.FiveCh.audioRecordWrapperFactory
    }

    /**
     * Android標準のマイクとして ステレオorモノラル録音する． [isStereo]
     */
    fun droid(isStereo: Boolean = true): ThinkletMic = object : ThinkletMic {
        override fun getAudioSettingsPatcher(): ThinkletAudioSettingsPatcher =
            customPatcher(isStereo)

        override fun getAudioRecordWrapperFactory(): ThinkletAudioRecordWrapperFactory? = null

    }

    /**
     * XFEを用いた録音を行う [audioManager] は必須．
     */
    fun xfe(audioManager: AudioManager): ThinkletMic = ThinkletMics.Xfe(audioManager)

    private fun customPatcher(isStereo: Boolean = true): ThinkletAudioSettingsPatcher {
        return object : ThinkletAudioSettingsPatcher {
            override fun getAudioSource(defaultAudioSource: Int): Int = defaultAudioSource
            override fun getAudioFormat(defaultAudioFormat: Int): Int = defaultAudioFormat
            override fun getChannelCount(defaultChannelCount: Int): Int = if (isStereo) 2 else 1
            override fun getSampleRate(
                audioSpecRange: Range<Int>,
                defaultSampleRate: Int
            ): Int = defaultSampleRate
        }
    }
}
