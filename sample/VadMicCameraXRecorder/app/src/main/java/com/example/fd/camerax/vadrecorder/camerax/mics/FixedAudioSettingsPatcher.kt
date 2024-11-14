package com.example.fd.camerax.vadrecorder.camerax.mics

import ai.fd.thinklet.camerax.ThinkletAudioSettingsPatcher
import android.media.AudioFormat
import android.media.MediaRecorder
import android.util.Range

internal class FixedAudioSettingsPatcher : ThinkletAudioSettingsPatcher {
    override fun getAudioSource(defaultSetting: Int): Int = MediaRecorder.AudioSource.MIC

    override fun getAudioFormat(defaultSetting: Int): Int = AudioFormat.ENCODING_PCM_16BIT

    override fun getChannelCount(defaultSetting: Int): Int = 1

    override fun getSampleRate(audioSpecRange: Range<Int>, defaultSampleRate: Int): Int = 16000
}
