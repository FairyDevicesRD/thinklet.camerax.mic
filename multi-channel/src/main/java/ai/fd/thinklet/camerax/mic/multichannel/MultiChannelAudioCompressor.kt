package ai.fd.thinklet.camerax.mic.multichannel

import ai.fd.thinklet.sdk.audio.MultiChannelAudioRecord
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * An utility class to compress multi channel audio data to monaural or stereo.
 */
object MultiChannelAudioCompressor {
    fun compressPcm16bitAudio(
        sourceByteArray: ByteArray,
        sourceChannelCount: MultiChannelAudioRecord.Channel,
        stereoOutput: Boolean
    ): ByteArray? = when (sourceChannelCount) {
        MultiChannelAudioRecord.Channel.CHANNEL_SIX ->
            if (stereoOutput) {
                sourceByteArray.get6chTo2chPcm16bit()
            } else {
                sourceByteArray.get6chTo1chPcm16bit()
            }

        MultiChannelAudioRecord.Channel.CHANNEL_FIVE ->
            if (stereoOutput) {
                sourceByteArray.get5chTo2chPcm16bit()
            } else {
                sourceByteArray.get5chTo1chPcm16bit()
            }
    }

    private fun ByteArray.toShortArray(): ShortArray? = runCatching {
        ShortArray(this.size / 2) {
            (this[it * 2] + (this[(it * 2) + 1].toInt() shl 8)).toShort()
        }
    }.getOrNull()

    private fun ByteArray.get6chTo1chPcm16bit(): ByteArray? = runCatching {
        val shortArray = this.toShortArray() ?: return null
        val byteBuffer = ByteBuffer.allocate(shortArray.size / 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        for (i in shortArray.indices step 6) {
            byteBuffer.putShort(
                ((shortArray[i] + shortArray[i + 1] + shortArray[i + 2] +
                        shortArray[i + 4] + shortArray[i + 5]) / 5).toShort()
            )
        }
        byteBuffer.array()
    }.getOrNull()

    private fun ByteArray.get6chTo2chPcm16bit(): ByteArray? = runCatching {
        val shortArray = this.toShortArray() ?: return null
        val byteBuffer = ByteBuffer.allocate(shortArray.size * 2 / 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        for (i in shortArray.indices step 6) {
            // 左側
            byteBuffer.putShort(
                ((shortArray[i + 5] + shortArray[i + 4] + shortArray[i]) / 3).toShort()
            )
            // 右側
            byteBuffer.putShort(
                ((shortArray[i + 1] + shortArray[i + 2]) / 2).toShort()
            )
        }
        byteBuffer.array()
    }.getOrNull()

    private fun ByteArray.get5chTo1chPcm16bit(): ByteArray? = runCatching {
        val shortArray = this.toShortArray() ?: return null
        val byteBuffer = ByteBuffer.allocate(this.size / 5)
        byteBuffer.order(ByteOrder.nativeOrder())

        for (i in shortArray.indices step 5) {
            byteBuffer.putShort(
                ((shortArray[i] + shortArray[i + 1] + shortArray[i + 2] +
                        shortArray[i + 3] + shortArray[i + 4]) / 5).toShort()
            )
        }
        byteBuffer.array()
    }.getOrNull()

    private fun ByteArray.get5chTo2chPcm16bit(): ByteArray? = runCatching {
        val shortArray = this.toShortArray() ?: return null
        val byteBuffer = ByteBuffer.allocate(this.size * 2 / 5)
        byteBuffer.order(ByteOrder.nativeOrder())
        for (i in shortArray.indices step 5) {
            // 左側
            byteBuffer.putShort(
                ((shortArray[i + 4] + shortArray[i + 3] + shortArray[i]) / 3).toShort()
            )
            // 右側
            byteBuffer.putShort(
                ((shortArray[i + 1] + shortArray[i + 2]) / 2).toShort()
            )
        }
        byteBuffer.array()
    }.getOrNull()
}
