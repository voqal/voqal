package dev.voqal.provider.clients.picovoice.natives

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import dev.voqal.ide.logging.LoggerFactory.VoqalLogger
import dev.voqal.provider.clients.picovoice.error.PicovoiceError

@Suppress("FunctionName")
interface PicovoiceNative : Library {

    companion object {
        fun throwIfError(log: VoqalLogger, native: PicovoiceNative, status: Int) {
            if (status != 0) {
                var errorMessage = native.pv_status_to_string(status)
                val messageStackRef = PointerByReference()
                val messageStackDepthRef = IntByReference()
                val errorStatus = native.pv_get_error_stack(messageStackRef, messageStackDepthRef)
                if (errorStatus == 0) {
                    val messageStack = messageStackRef.value
                    val messageStackDepth = messageStackDepthRef.value
                    for (i in 0 until messageStackDepth) {
                        val message = messageStack.getPointer((i * Native.POINTER_SIZE).toLong()).getString(0)
                        errorMessage += ", $message"
                    }
                    native.pv_free_error_stack(messageStack)
                    log.warn(errorMessage)
                } else {
                    log.error("Error getting error stack, status: $errorStatus")
                }

                throw PicovoiceError(errorMessage)
            }
        }
    }

    fun pv_status_to_string(status: Int): String
    fun pv_free_error_stack(pointer: Pointer)
    fun pv_get_error_stack(): Pointer
    fun pv_get_error_stack(messageStack: PointerByReference, messageStackDepth: IntByReference): Int
}
