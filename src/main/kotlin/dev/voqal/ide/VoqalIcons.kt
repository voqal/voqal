package dev.voqal.ide

import com.intellij.openapi.util.IconLoader
import com.intellij.ui.JBColor
import java.awt.Color
import javax.swing.Icon

object VoqalIcons {
    val logo = IconLoader.getIcon("/icons/logo.svg", VoqalIcons::class.java)
    val logoIdle = IconLoader.getIcon("/icons/logo-idle.svg", VoqalIcons::class.java)
    val logoListening = IconLoader.getIcon("/icons/logo-listening.svg", VoqalIcons::class.java)
    val logoProcessing = IconLoader.getIcon("/icons/logo-processing.svg", VoqalIcons::class.java)
    val logoEditing = IconLoader.getIcon("/icons/logo-editing.svg", VoqalIcons::class.java)
    val logoOffline = IconLoader.getIcon("/icons/logo-offline.svg", VoqalIcons::class.java)
    val logoError = IconLoader.getIcon("/icons/logo-error.svg", VoqalIcons::class.java)

    val Default: Icon = IconLoader.getIcon("/icons/voqal-avatar.svg", VoqalIcons::class.java)
    val Error: Icon = IconLoader.getIcon("/icons/voqal-avatar-error.svg", VoqalIcons::class.java)
    val Debug: Icon = IconLoader.getIcon("/icons/voqal-avatar-debug.svg", VoqalIcons::class.java)
    val User: Icon = IconLoader.getIcon("/icons/user.svg", VoqalIcons::class.java)

    val volume: Icon = IconLoader.getIcon("/icons/volume.svg", VoqalIcons::class.java)
    val microphone: Icon = IconLoader.getIcon("/icons/microphone.svg", VoqalIcons::class.java)

    object Compute {
        val ollama: Icon = IconLoader.getIcon("/icons/compute/ollama.svg", VoqalIcons::class.java)
        val google: Icon = IconLoader.getIcon("/icons/compute/google.svg", VoqalIcons::class.java)
        val openai: Icon = IconLoader.getIcon("/icons/compute/openai.svg", VoqalIcons::class.java)
        val huggingface: Icon = IconLoader.getIcon("/icons/compute/huggingface.svg", VoqalIcons::class.java)
        val mistralai: Icon = IconLoader.getIcon("/icons/compute/mistralai.svg", VoqalIcons::class.java)
        val groq: Icon = IconLoader.getIcon("/icons/compute/groq.svg", VoqalIcons::class.java)
        val t: Icon = IconLoader.getIcon("/icons/compute/t.svg", VoqalIcons::class.java)
        val globe: Icon = IconLoader.getIcon("/icons/compute/globe.svg", VoqalIcons::class.java)
        val anthropic: Icon = IconLoader.getIcon("/icons/compute/anthropic.svg", VoqalIcons::class.java)
        val deepseek: Icon = IconLoader.getIcon("/icons/compute/deepseek.svg", VoqalIcons::class.java)
        val fireworks: Icon = IconLoader.getIcon("/icons/compute/fireworks.svg", VoqalIcons::class.java)
    }

    val DARK_RED = JBColor(JBColor.PINK, Color(0x5E3838))
}
