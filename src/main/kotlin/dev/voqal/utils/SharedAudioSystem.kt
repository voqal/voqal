package dev.voqal.utils

import com.intellij.openapi.project.Project
import dev.voqal.services.getVoqalLogger
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Mixer
import javax.sound.sampled.TargetDataLine

/**
 * Allows multiple project IDEs to share a single [SharedAudioCapture].
 */
object SharedAudioSystem {

    private var line: TargetDataLine? = null
    private var usageCount = 0
    private val listeners = WeakHashMap<Project, LinkedBlockingQueue<ByteArray>>()
    private var running: Boolean = false

    @Synchronized
    fun getTargetDataLine(project: Project, format: AudioFormat, info: Mixer.Info? = null): SharedAudioLine {
        val log = project.getVoqalLogger(this::class)
        if (line == null) {
            log.debug("Creating new microphone line")
            line = AudioSystem.getTargetDataLine(format, info).also {
                it.open(format)
                it.start()
                running = true
                startReadingThread(it)
            }
        } else {
            log.debug("Reusing existing microphone line")
        }
        usageCount++
        log.debug("Microphone line usage count: $usageCount")

        listeners[project] = LinkedBlockingQueue()
        return SharedAudioLine(project)
    }

    private fun startReadingThread(line: TargetDataLine) {
        Thread {
            val buffer = ByteArray(SharedAudioCapture.BUFFER_SIZE)
            while (running) {
                val numBytesRead = line.read(buffer, 0, buffer.size)
                if (numBytesRead > 0) {
                    listeners.values.forEach { queue ->
                        queue.offer(buffer.copyOf())
                    }
                }
            }
        }.apply {
            name = "SharedAudioSystem Reader"
            isDaemon = true
        }.start()
    }

    private fun releaseLine(project: Project) {
        val log = project.getVoqalLogger(this::class)
        if (--usageCount == 0) {
            log.debug("Closing microphone line")
            running = false
            line?.stop()
            line?.close()
            line = null
            listeners.clear()
        } else {
            log.debug("Microphone line usage decremented to $usageCount")
        }
    }

    class SharedAudioLine(private val project: Project) : AutoCloseable {

        private var closed = false

        fun read(b: ByteArray, off: Int, len: Int): Int {
            require(off == 0) { "Only offset 0 is supported" }
            require(len == SharedAudioCapture.BUFFER_SIZE) { "Only buffer size is supported" }
            val queue = listeners[project] ?: throw IllegalStateException("No queue available for this project")
            val data = queue.take()
            System.arraycopy(data, 0, b, off, len)
            return len
        }

        @Synchronized
        override fun close() {
            if (closed) {
                return
            }
            closed = true
            releaseLine(project)
            listeners.remove(project)
        }
    }
}
