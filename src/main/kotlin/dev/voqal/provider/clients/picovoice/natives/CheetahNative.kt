package dev.voqal.provider.clients.picovoice.natives

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import java.io.File

@Suppress("FunctionName")
interface CheetahNative : PicovoiceNative {
    companion object {
        fun getINSTANCE(dll: File): CheetahNative {
            return Native.load(dll.absolutePath, CheetahNative::class.java) as CheetahNative
        }
    }

    fun pv_sample_rate(): Int
    fun pv_cheetah_version(): String
    fun pv_cheetah_frame_length(): Int

    /**
     * Constructor.
     *
     * @param access_key AccessKey obtained from Picovoice Console (https://picovoice.ai/console/)
     * @param model_path Absolute path to the file containing model parameters.
     * @param endpoint_duration_sec Duration of endpoint in seconds. A speech endpoint is detected when there is a segment
     * of audio (with a duration specified herein) after an utterance without any speech in it. Set to `0` to disable
     * endpoint detection.
     * @param enable_automatic_punctuation Set to `true` to enable automatic punctuation insertion.
     * @param[out] object Constructed instance of Cheetah.
     * @return Status code. Returns `PV_STATUS_INVALID_ARGUMENT`, `PV_STATUS_IO_ERROR`, `PV_STATUS_OUT_OF_MEMORY`,
     * `PV_STATUS_RUNTIME_ERROR`, `PV_STATUS_ACTIVATION_ERROR`, `PV_STATUS_ACTIVATION_LIMIT_REACHED`,
     * `PV_STATUS_ACTIVATION_THROTTLED`, or `PV_STATUS_ACTIVATION_REFUSED` on failure
     */
    fun pv_cheetah_init(
        accessKey: String,
        modelPath: String,
        endpointDurationSec: Float,
        enableAutomaticPunctuation: Boolean,
        `object`: PointerByReference
    ): Int

    fun pv_cheetah_delete(`object`: Pointer)

    /**
     * Processes a frame of audio and returns newly-transcribed text and a flag indicating if an endpoint has been detected.
     * Upon detection of an endpoint, the client may invoke `pv_cheetah_flush()` to retrieve any remaining transcription.
     * The caller is responsible for freeing the transcription buffer.
     *
     * @param object Cheetah object.
     * @param pcm A frame of audio samples. The number of samples per frame can be attained by calling
     * `pv_cheetah_frame_length()`. The incoming audio needs to have a sample rate equal to `pv_sample_rate()` and be 16-bit
     * linearly-encoded. Cheetah operates on single-channel audio.
     * @param[out] transcript Any newly-transcribed speech. If none is available then an empty string is returned.
     * @param[out] is_endpoint Flag indicating if an endpoint has been detected. If endpointing is disabled then set to
     * `NULL`.
     * @return Status code. Returns `PV_STATUS_INVALID_ARGUMENT` or `PV_STATUS_OUT_OF_MEMORY`,
     * `PV_STATUS_RUNTIME_ERROR`, `PV_STATUS_ACTIVATION_ERROR`, `PV_STATUS_ACTIVATION_LIMIT_REACHED`,
     * `PV_STATUS_ACTIVATION_THROTTLED`, or `PV_STATUS_ACTIVATION_REFUSED` on failure
     */
    fun pv_cheetah_process(
        `object`: Pointer,
        pcm: ShortArray,
        transcript: PointerByReference,
        isEndpoint: IntByReference
    ): Int

    /**
     * Marks the end of the audio stream, flushes internal state of the object, and returns any remaining transcript. The
     * caller is responsible for freeing the transcription buffer.
     *
     * @param object Cheetah object.
     * @param transcript transcript Any remaining transcribed text. If none is available then an empty string is returned.
     * @return Status code. Returns `PV_STATUS_INVALID_ARGUMENT` or `PV_STATUS_OUT_OF_MEMORY`,
     * `PV_STATUS_RUNTIME_ERROR`, `PV_STATUS_ACTIVATION_ERROR`, `PV_STATUS_ACTIVATION_LIMIT_REACHED`,
     * `PV_STATUS_ACTIVATION_THROTTLED`, or `PV_STATUS_ACTIVATION_REFUSED` on failure
     */
    fun pv_cheetah_flush(`object`: Pointer, transcript: PointerByReference): Int

    /**
     * Deletes transcript returned from `pv_cheetah_process()` or `pv_cheetah_flush()`
     *
     * @param transcript transcription string returned from `pv_cheetah_process()` or `pv_cheetah_flush()`
     */
    fun pv_cheetah_transcript_delete(transcript: Pointer)
}
