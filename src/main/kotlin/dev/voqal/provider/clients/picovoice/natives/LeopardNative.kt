package dev.voqal.provider.clients.picovoice.natives

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import java.io.File

@Suppress("FunctionName")
interface LeopardNative : PicovoiceNative {
    companion object {
        fun getINSTANCE(dll: File): LeopardNative {
            return Native.load(dll.absolutePath, LeopardNative::class.java) as LeopardNative
        }
    }

    /**
     * Constructor.
     *
     * @param accessKey AccessKey obtained from Picovoice Console (https://console.picovoice.ai/)
     * @param modelPath Absolute path to the file containing model parameters.
     * @param enableAutomaticPunctuation Set to `true` to enable automatic punctuation insertion.
     * @param enableDiarization Set to `true` to enable speaker diarization, which allows Leopard to differentiate speakers
     * as part of the transcription process. Word metadata will include a `speaker_tag` to identify unique speakers.
     * @param object object Constructed instance of Leopard.
     * @return Status code. Returns `PV_STATUS_OUT_OF_MEMORY`, `PV_STATUS_IO_ERROR`, `PV_STATUS_INVALID_ARGUMENT`,
     * `PV_STATUS_RUNTIME_ERROR`, `PV_STATUS_ACTIVATION_ERROR`, `PV_STATUS_ACTIVATION_LIMIT_REACHED`,
     * `PV_STATUS_ACTIVATION_THROTTLED`, or `PV_STATUS_ACTIVATION_REFUSED` on failure.
     */
    fun pv_leopard_init(
        accessKey: String,
        modelPath: String,
        enableAutomaticPunctuation: Boolean,
        enableDiarization: Boolean,
        `object`: PointerByReference
    ): Int

    fun pv_leopard_delete(`object`: Pointer)

    /**
     * Processes a given audio data and returns its transcription. The caller is responsible for freeing the transcription
     * buffer.
     *
     * @param object Leopard object.
     * @param pcm Audio data. The audio needs to have a sample rate equal to `pv_sample_rate()` and be 16-bit
     * linearly-encoded. This function operates on single-channel audio.
     * @param numSamples Number of audio samples to process.
     * @param transcript Inferred transcription.
     * @param numWords Number of transcribed words
     * @param words Transcribed words and their associated metadata.
     * @return Status code. Returns `PV_STATUS_OUT_OF_MEMORY`, `PV_STATUS_IO_ERROR`, `PV_STATUS_INVALID_ARGUMENT`,
     * `PV_STATUS_RUNTIME_ERROR`, `PV_STATUS_ACTIVATION_ERROR`, `PV_STATUS_ACTIVATION_LIMIT_REACHED`,
     * `PV_STATUS_ACTIVATION_THROTTLED`, or `PV_STATUS_ACTIVATION_REFUSED` on failure
     */
    fun pv_leopard_process(
        `object`: Pointer,
        pcm: ShortArray,
        numSamples: Int,
        transcript: PointerByReference,
        numWords: IntByReference,
        words: PointerByReference
    ): Int

    /**
     * Processes a given audio file and returns its transcription. The caller is responsible for freeing the transcription
     * buffer.
     *
     * @param object Leopard object.
     * @param audioPath Absolute path to the audio file. The file needs to have a sample rate equal to or greater than
     * `pv_sample_rate()`. The supported formats are: `3gp (AMR)`, `FLAC`, `MP3`, `MP4/m4a (AAC)`, `Ogg`, `WAV`, `WebM`.
     * Files with stereo audio are mixed into a single mono channel and then processed.
     * @param transcript Inferred transcription.
     * @param numWords Number of transcribed words
     * @param words Transcribed words and their associated metadata.
     * @return Status code. Returns `PV_STATUS_OUT_OF_MEMORY`, `PV_STATUS_IO_ERROR`, `PV_STATUS_INVALID_ARGUMENT`,
     * `PV_STATUS_RUNTIME_ERROR`, `PV_STATUS_ACTIVATION_ERROR`, `PV_STATUS_ACTIVATION_LIMIT_REACHED`,
     * `PV_STATUS_ACTIVATION_THROTTLED`, or `PV_STATUS_ACTIVATION_REFUSED` on failure
     */
    fun pv_leopard_process_file(
        `object`: Pointer,
        audioPath: String,
        transcript: PointerByReference,
        numWords: IntByReference,
        words: PointerByReference
    ): Int

    /**
     * Deletes transcript returned from `pv_leopard_process()` or `pv_leopard_process_file()`
     *
     * @param transcript transcription string returned from `pv_leopard_process()` or `pv_leopard_process_file()`
     */
    fun pv_leopard_transcript_delete(transcript: Pointer)

    /**
     * Deletes words returned from `pv_leopard_process()` or `pv_leopard_process_file()`
     *
     * @param words transcribed words returned from `pv_leopard_process()` or `pv_leopard_process_file()`
     */
    fun pv_leopard_words_delete(words: Pointer)

    fun pv_leopard_version(): String
}
