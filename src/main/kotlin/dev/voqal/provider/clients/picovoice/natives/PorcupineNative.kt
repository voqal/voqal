package dev.voqal.provider.clients.picovoice.natives

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import java.io.File

@Suppress("FunctionName")
interface PorcupineNative : PicovoiceNative {
    companion object {
        fun getINSTANCE(dll: File): PorcupineNative {
            return Native.load(dll.absolutePath, PorcupineNative::class.java) as PorcupineNative
        }
    }

    fun pv_porcupine_init(
        accessKey: String,
        modelPath: String,
        numKeywords: Int,
        keywordPaths: Array<String>,
        sensitivities: FloatArray,
        `object`: PointerByReference
    ): Int

    fun pv_porcupine_delete(`object`: Pointer)
    fun pv_porcupine_process(`object`: Pointer, pcm: ShortArray, keywordIndex: IntByReference): Int
    fun pv_porcupine_version(): String
    fun pv_sample_rate(): Int
    fun pv_porcupine_frame_length(): Int
}
