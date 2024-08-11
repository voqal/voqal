package dev.voqal.provider.clients.picovoice.natives

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.FloatByReference
import com.sun.jna.ptr.PointerByReference
import java.io.File

@Suppress("FunctionName")
interface CobraNative : PicovoiceNative {
    companion object {
        fun getINSTANCE(dll: File): CobraNative {
            return Native.load(dll.absolutePath, CobraNative::class.java) as CobraNative
        }
    }

    fun pv_cobra_version(): String
    fun pv_cobra_frame_length(): Int
    fun pv_cobra_init(accessKey: String, `object`: PointerByReference): Int
    fun pv_cobra_delete(`object`: Pointer)
    fun pv_cobra_process(`object`: Pointer, pcm: ShortArray, isVoiced: FloatByReference): Int
    fun pv_free(pointer: Pointer)
    fun pv_get_sdk(): String
    fun pv_log_disable()
    fun pv_log_enable()
    fun pv_sample_rate(): Int
    fun pv_set_home_dir(homeDir: String)
    fun pv_set_sdk(sdk: String)
}
