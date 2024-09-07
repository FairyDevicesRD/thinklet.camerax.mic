package ai.fd.thinklet.camerax.mic.multichannel

import ai.fd.thinklet.sdk.audio.MultiChannelAudioRecord.Channel
import android.media.AudioManager.AudioRecordingCallback
import android.media.AudioRecord
import android.media.AudioRecordingConfiguration
import android.media.AudioTimestamp
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.video.audio.wrapper.AudioRecordWrapper
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import kotlin.math.min

/**
 * An implementation of [AudioRecordWrapper] which uses an [AudioRecord] created by
 * [ai.fd.thinklet.sdk.audio.MultiChannelAudioRecord] to　input 5 or 6 channels of audio and
 * compresses that data to stereo or mono.
 */
internal class MultiChannelAudioRecordWrapper(
    private val audioRecord: AudioRecord,
    private val sourceChannelCount: Channel,
    private val sourceBufferSize: Int,
    private val isStereoOutput: Boolean
) : AudioRecordWrapper() {

    private val sourceBuffer: ByteArray = ByteArray(sourceBufferSize)

    override fun getState(): Int = audioRecord.state

    override fun release() = audioRecord.release()

    override fun startRecording() = audioRecord.startRecording()

    override fun getRecordingState(): Int = audioRecord.recordingState

    override fun stop() = audioRecord.stop()

    override fun read(byteBuffer: ByteBuffer, bufferSize: Int): Int {
        val readCount = audioRecord.read(sourceBuffer, 0, sourceBufferSize)
        if (readCount > 0) {
            return compressChannel(
                sourceBuffer, sourceChannelCount, isStereoOutput, byteBuffer,
                bufferSize
            )
        }
        Log.w(TAG, "Read data from multi channel audio is empty!")
        return 0
    }

    override fun getAudioSessionId(): Int = audioRecord.audioSessionId

    override fun getTimestamp(audioTimestamp: AudioTimestamp, timeBase: Int): Int =
        audioRecord.getTimestamp(audioTimestamp, timeBase)

    @RequiresApi(29)
    override fun getActiveRecordingConfiguration(): AudioRecordingConfiguration? =
        audioRecord.activeRecordingConfiguration

    @RequiresApi(29)
    override fun registerAudioRecordingCallback(
        executor: Executor,
        callback: AudioRecordingCallback
    ) = audioRecord.registerAudioRecordingCallback(executor, callback)

    @RequiresApi(29)
    override fun unregisterAudioRecordingCallback(callback: AudioRecordingCallback) =
        audioRecord.unregisterAudioRecordingCallback(callback)

    companion object {
        private const val TAG = "THINKLET-CameraX-MultiChannel"

        private fun compressChannel(
            sourceByteArray: ByteArray,
            sourceChannelCount: Channel,
            isStereoOutput: Boolean,
            outByteBuffer: ByteBuffer,
            outBufferSize: Int
        ): Int {
            val compressedData = MultiChannelAudioCompressor
                .compressPcm16bitAudio(sourceByteArray, sourceChannelCount, isStereoOutput)
            if (compressedData == null) {
                Log.w(TAG, "Compressed data is empty!")
                return 0
            }
            val writtenDataSize = min(compressedData.size, outBufferSize)
            outByteBuffer.put(compressedData, 0, writtenDataSize)
            return writtenDataSize
        }
    }
}
