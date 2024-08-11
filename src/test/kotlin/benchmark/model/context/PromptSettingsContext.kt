package benchmark.model.context

import dev.voqal.assistant.context.VoqalContext
import dev.voqal.config.settings.PromptSettings

class PromptSettingsContext(val settings: PromptSettings) : VoqalContext
