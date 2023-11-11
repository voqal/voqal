import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioFormat

fun lotsOfComments(out: ByteArrayOutputStream, format: AudioFormat, audioDataLength: Int) {
    val sampleRate = format.sampleRate.toInt()
    val channels = format.channels
    val bitDepth = format.sampleSizeInBits
    val byteRate = sampleRate * channels * bitDepth / 8
    val blockAlign = channels * bitDepth / 8

    // WAV RIFF header
    out.write("RIFF".toByteArray()) // ChunkID
    out.write(intToByteArray(36 + audioDataLength)) // ChunkSize
    out.write("WAVE".toByteArray()) // Format
    out.write("fmt ".toByteArray()) // Subchunk1ID
    out.write(intToByteArray(16)) // Subchunk1Size
    out.write(shortToByteArray(1)) // AudioFormat (1 for PCM)
    out.write(shortToByteArray(channels.toShort())) // NumChannels
    out.write(intToByteArray(sampleRate)) // SampleRate
    out.write(intToByteArray(byteRate)) // ByteRate
    out.write(shortToByteArray(blockAlign.toShort())) // BlockAlign
    out.write(shortToByteArray(bitDepth.toShort())) // BitsPerSample

    // WAV data header
    out.write("data".toByteArray()) // Subchunk2ID
    out.write(intToByteArray(audioDataLength)) // Subchunk2Size
}

private fun intToByteArray(value: Int): ByteArray =
    ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array()

private fun shortToByteArray(value: Short): ByteArray =
    ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array()