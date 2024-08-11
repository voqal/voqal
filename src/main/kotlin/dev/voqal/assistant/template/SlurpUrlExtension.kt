package dev.voqal.assistant.template

import dev.voqal.assistant.VoqalDirective
import dev.voqal.services.VoqalConfigService.Companion.toHeaderMap
import dev.voqal.services.VoqalDirectiveService
import dev.voqal.services.getVoqalLogger
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.pebbletemplates.pebble.extension.AbstractExtension
import io.pebbletemplates.pebble.extension.Function
import io.pebbletemplates.pebble.template.EvaluationContext
import io.pebbletemplates.pebble.template.PebbleTemplate
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.Instant

class SlurpUrlExtension : AbstractExtension() {

    private val cache = mutableMapOf<String, Pair<Instant, Any?>>()

    override fun getFunctions() = mapOf(
        "slurpUrl" to SlurpUrlFunction()
    )

    inner class SlurpUrlFunction : Function {

        private val client = HttpClient {
            install(HttpTimeout) { requestTimeoutMillis = 30_000 }
        }

        override fun getArgumentNames() = listOf("url", "cacheTime", "headers")

        private fun parseDuration(duration: String): Duration {
            val number = duration.substring(0, duration.length - 1).toLong()
            return when (duration.last()) {
                'm' -> Duration.ofMinutes(number)
                'h' -> Duration.ofHours(number)
                's' -> Duration.ofSeconds(number)
                else -> throw IllegalArgumentException("Unknown time unit in duration")
            }
        }

        override fun execute(
            args: Map<String, Any?>,
            self: PebbleTemplate,
            context: EvaluationContext,
            lineNumber: Int
        ): Any? = runBlocking {
            val project = (context.getVariable("directive") as? VoqalDirective)?.project ?: return@runBlocking null
            val log = project.getVoqalLogger(this::class)
            val url = args["url"] as? String ?: return@runBlocking null
            val cacheTime = parseDuration(args["cacheTime"] as? String ?: "0m")
            val headerMap = toHeaderMap(args["headers"] as? String ?: "")

            // Check if the URL is in the cache and if it is still valid
            val cachedResponse = cache[url]
            if (cachedResponse != null && Instant.now().isBefore(cachedResponse.first.plus(cacheTime))) {
                log.info("Using cached response for $url")
                return@runBlocking cachedResponse.second
            }

            log.info("Slurping $url with headers $headerMap")
            try {
                val response = client.get(url) {
                    headers { headerMap.forEach { (key, value) -> append(key, value) } }
                }
                val responseBody = response.bodyAsText()
                val json = JsonObject(responseBody)
                val parsedResponse = VoqalDirectiveService.convertJsonElementToMap(
                    Json.parseToJsonElement(json.toString())
                )
                // Cache the new response with current time
                cache[url] = Instant.now() to parsedResponse
                return@runBlocking parsedResponse
            } catch (e: Exception) {
                log.error("Error fetching URL: $url", e)
                return@runBlocking null
            }
        }
    }
}
