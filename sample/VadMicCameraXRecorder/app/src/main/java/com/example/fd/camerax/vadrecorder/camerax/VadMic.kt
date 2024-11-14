package com.example.fd.camerax.vadrecorder.camerax

import ai.fd.thinklet.camerax.ThinkletAudioRecordWrapperFactory
import ai.fd.thinklet.camerax.ThinkletAudioSettingsPatcher
import ai.fd.thinklet.camerax.ThinkletMic
import ai.fd.thinklet.camerax.mic.ThinkletMics
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.example.fd.camerax.vadrecorder.camerax.mics.FixedAudioSettingsPatcher
import com.example.fd.camerax.vadrecorder.camerax.mics.Vad
import com.example.fd.camerax.vadrecorder.camerax.mics.VadAudioRecordWrapperFactory

fun ThinkletMics.vadMic(context: Context, lifecycleOwner: LifecycleOwner): ThinkletMic =
    VadMic(context, lifecycleOwner)

internal class VadMic(context: Context, lifecycleOwner: LifecycleOwner) : ThinkletMic {
    private val vad = Vad(context, lifecycleOwner)

    override fun getAudioSettingsPatcher(): ThinkletAudioSettingsPatcher? =
        FixedAudioSettingsPatcher()

    override fun getAudioRecordWrapperFactory(): ThinkletAudioRecordWrapperFactory? =
        VadAudioRecordWrapperFactory { vad.isSpeech(it) }
}
