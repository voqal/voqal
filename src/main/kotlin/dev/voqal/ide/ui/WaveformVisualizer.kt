package dev.voqal.ide.ui

import com.intellij.openapi.Disposable
import com.intellij.ui.JBColor
import dev.voqal.utils.SharedAudioCapture
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.util.concurrent.LinkedBlockingQueue
import javax.swing.JPanel

class WaveformVisualizer(
    private val audioCapture: SharedAudioCapture
) : JPanel(), Runnable, Disposable, SharedAudioCapture.AudioDataListener {

    private val audioQueue = LinkedBlockingQueue<ByteArray>()
    private var running = false
    private val maxSamples = SharedAudioCapture.BUFFER_SIZE
    private val height = 50
    private val yScale = Short.MAX_VALUE * 2 / height
    private val xPoints = IntArray(maxSamples) { it }
    private val yPoints = IntArray(maxSamples) { 0 }

    init {
        preferredSize = Dimension(100, height)
        setToZero()
        audioCapture.registerListener(this)
    }

    override fun isTestListener() = true
    override fun isLiveDataListener() = true

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        //draw the data points
        g2d.color = JBColor.BLUE
        g2d.drawPolyline(xPoints, yPoints, maxSamples)
    }

    override fun run() {
        running = true
        while (running) {
            val b = audioQueue.take()
            for (i in b.indices step 2) {
                val sample = ((b[i + 1].toInt() shl 8) or (b[i].toInt() and 0x00FF))
                yPoints[i shr 1] = (sample - Short.MIN_VALUE) / yScale
            }

            repaint()
        }
    }

    fun start() {
        Thread(this).apply {
            name = "Voqal Waveform Visualizer"
            isDaemon = true
            start()
        }
    }

    override fun onAudioData(data: ByteArray, detection: SharedAudioCapture.AudioDetection) {
        audioQueue.put(data)
    }

    override fun dispose() {
        running = false
        audioCapture.removeListener(this)
    }

    private fun setToZero() {
        val zeroLevel = height / 2
        for (i in 0 until maxSamples) {
            yPoints[i] = zeroLevel
        }
        repaint()
    }
}
