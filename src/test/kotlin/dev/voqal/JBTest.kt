package dev.voqal

import com.intellij.openapi.components.service
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.WaitFor
import dev.voqal.assistant.memory.MemorySystem
import dev.voqal.assistant.memory.local.LocalMemorySystem
import dev.voqal.assistant.memory.thread.ThreadMemorySystem
import dev.voqal.config.VoqalConfig
import dev.voqal.config.settings.LanguageModelSettings
import dev.voqal.config.settings.LanguageModelSettings.LMProvider
import dev.voqal.config.settings.LanguageModelsSettings
import dev.voqal.config.settings.PromptLibrarySettings
import dev.voqal.config.settings.PromptSettings
import dev.voqal.ide.logging.LoggerFactory
import dev.voqal.services.VoqalConfigService
import dev.voqal.services.getVoqalLogger
import io.vertx.junit5.VertxTestContext
import java.io.File

abstract class JBTest : BasePlatformTestCase() {

    companion object {
        val lmProvider = LMProvider.lenientValueOf(System.getenv("VQL_MODEL_PROVIDER") ?: "openai")
        val TEST_CONFIG = VoqalConfig(
            languageModelsSettings = LanguageModelsSettings(
                models = listOf(
                    LanguageModelSettings(
                        provider = lmProvider,
                        providerKey = System.getenv("VQL_MODEL_KEY") ?: "YOUR_OPENAI_KEY",
                        modelName = System.getenv("VQL_MODEL_NAME") ?: "gpt-4o-mini",
                        seed = 1234567890,
                        temperature = 0.0,
                        observabilityProvider = LanguageModelSettings.OProvider.valueOf(
                            System.getenv("VQL_OBSERVABILITY_PROVIDER") ?: "None"
                        ),
                        observabilityKey = System.getenv("VQL_OBSERVABILITY_KEY") ?: "",
                        observabilityUserId = System.getenv("VQL_OBSERVABILITY_USER_ID") ?: ""
                    )
                )
            ),
            promptLibrarySettings = PromptLibrarySettings(
                listOf(
                    PromptSettings(promptName = "Edit Mode", languageModel = lmProvider.displayName),
                    PromptSettings(promptName = "Idle Mode", languageModel = lmProvider.displayName)
                )
            )
        )

        init {
            LoggerFactory.outputFile = File(System.getProperty("java.io.tmpdir"), "voqal-test.log").apply {
                delete()
                createNewFile()
            }
        }
    }

    val log by lazy { project.getVoqalLogger(this::class) }

    override fun setUp() {
        System.setProperty("VQL_TEST_MODE", "true")
        super.setUp()

        val configService = project.service<VoqalConfigService>()
        configService.setCachedConfig(TEST_CONFIG)
    }

    fun getMemorySystem(type: String = System.getenv("VQL_MODE") ?: "local"): MemorySystem {
        val memorySystem = when (type) {
            "local" -> LocalMemorySystem(project)
            "thread" -> ThreadMemorySystem(project)
            else -> throw IllegalStateException()
        }
        log.info("Testing memory system: $type")
        return memorySystem
    }

    fun errorOnTimeout(testContext: VertxTestContext, waitTime: Long = 60) {
        object : WaitFor((waitTime * 1000).toInt()) {
            override fun condition(): Boolean {
                PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue()
                return testContext.failed() || testContext.completed()
            }
        }
        if (testContext.failed()) {
            throw testContext.causeOfFailure()
        } else if (!testContext.completed()) {
            throw RuntimeException("Test timed out")
        }
    }
}
