package dev.voqal.status

import dev.voqal.ide.VoqalIcons
import javax.swing.Icon

/**
 * Represents the current status of the Voqal Assistant.
 */
enum class VoqalStatus {
    ERROR,
    DISABLED,
    IDLE,
    EDITING,
    SEARCHING;

    val icon: Icon
        get() {
            return when (this) {
                SEARCHING -> VoqalIcons.logo
                EDITING -> VoqalIcons.logoEditing
                DISABLED -> VoqalIcons.logoOffline
                ERROR -> VoqalIcons.logoError
                IDLE -> VoqalIcons.logoIdle
            }
        }
    val presentableText: String
        get() {
            return when (this) {
                DISABLED -> "Click to view controls"
                else -> "Status: " + name.lowercase().replaceFirstChar(Char::titlecase)
            }
        }
}
