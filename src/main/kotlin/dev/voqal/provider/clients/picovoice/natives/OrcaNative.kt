package dev.voqal.provider.clients.picovoice.natives

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.FloatByReference
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.LongByReference
import com.sun.jna.ptr.PointerByReference
import java.io.File

@Suppress("FunctionName")
interface OrcaNative : PicovoiceNative {
    companion object {
        fun getINSTANCE(dll: File): OrcaNative {
            return Native.load(dll.absolutePath, OrcaNative::class.java) as OrcaNative
        }
    }

    fun pv_orca_version(): String

    fun pv_orca_init(
        accessKey: String,
        modelPath: String,
        `object`: PointerByReference
    ): Int

    fun pv_orca_delete(`object`: Pointer)

    fun pv_orca_valid_characters(
        `object`: Pointer,
        numCharacters: IntByReference,
        characters: PointerByReference
    ): Int

    fun pv_orca_valid_characters_delete(characters: Pointer)

    fun pv_orca_sample_rate(`object`: Pointer, sampleRate: IntByReference): Int

    fun pv_orca_max_character_limit(`object`: Pointer, maxCharacterLimit: IntByReference): Int

    fun pv_orca_synthesize_params_init(`object`: PointerByReference): Int

    fun pv_orca_synthesize_params_delete(`object`: Pointer)

    fun pv_orca_synthesize_params_set_speech_rate(
        `object`: Pointer,
        speechRate: Float
    ): Int

    fun pv_orca_synthesize_params_get_speech_rate(
        `object`: Pointer,
        speechRate: FloatByReference
    ): Int

    fun pv_orca_synthesize_params_set_random_state(
        `object`: Pointer,
        randomState: Long
    ): Int

    fun pv_orca_synthesize_params_get_random_state(
        `object`: Pointer,
        randomState: LongByReference
    ): Int

    fun pv_orca_synthesize(
        `object`: Pointer,
        text: String,
        synthesizeParams: Pointer,
        numSamples: IntByReference,
        pcm: PointerByReference,
        numAlignments: IntByReference,
        alignments: PointerByReference
    ): Int

    fun pv_orca_synthesize_to_file(
        `object`: Pointer,
        text: String,
        synthesizeParams: Pointer,
        outputPath: String,
        numAlignments: IntByReference,
        alignments: PointerByReference
    ): Int

    fun pv_orca_stream_open(
        `object`: Pointer,
        synthesizeParams: Pointer,
        stream: PointerByReference
    ): Int

    fun pv_orca_stream_synthesize(
        stream: Pointer,
        text: String,
        numSamples: IntByReference,
        pcm: PointerByReference
    ): Int

    fun pv_orca_stream_flush(
        stream: Pointer,
        numSamples: IntByReference,
        pcm: PointerByReference
    ): Int

    fun pv_orca_stream_close(stream: Pointer)

    fun pv_orca_pcm_delete(pcm: Pointer)

    fun pv_orca_word_alignments_delete(
        numAlignments: Int,
        alignments: Pointer
    ): Int
}
