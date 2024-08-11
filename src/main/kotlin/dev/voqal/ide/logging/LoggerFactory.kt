package dev.voqal.ide.logging

import com.intellij.openapi.project.Project
import dev.voqal.services.logsTab
import org.slf4j.Logger
import org.slf4j.Marker
import java.io.File
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Custom logger factory to pipe plugin logs to the Voqal tool window.
 */
class LoggerFactory {
    companion object {
        private val loggers = WeakHashMap<Project, MutableMap<String, Logger>>()

        @JvmStatic
        fun <T> getLogger(project: Project, java: Class<out T>): Logger {
            val loggers = loggers.getOrPut(project) { mutableMapOf() }
            val name = java.name
            if (loggers.containsKey(name)) {
                return loggers[name]!!
            }
            return VoqalLogger(project, org.slf4j.LoggerFactory.getLogger(java), name).also {
                loggers[name] = it
            }
        }

        var outputFile: File? = null
    }

    class VoqalLogger(
        private val project: Project,
        private val l: Logger,
        private val name: String
    ) : Logger {

        private val logsTab by lazy { project.logsTab }
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .withZone(ZoneOffset.UTC)

        override fun getName(): String {
            return name
        }

        override fun isTraceEnabled(): Boolean {
            if (project.isDisposed) {
                return false
            }
            if (logsTab.logLevel == "TRACE") return true
            return l.isTraceEnabled
        }

        override fun isTraceEnabled(p0: Marker?): Boolean {
            return l.isTraceEnabled(p0)
        }

        override fun trace(p0: String?) {
            l.trace(p0)

            if (System.getProperty("VQL_TEST_MODE") == "true") {
                val logPattern = formatter.format(Instant.now()) + " TRACE " + name + " - " + p0 + "\n"
                outputFile?.appendText(logPattern)
                return
            }
            if (project.isDisposed) {
                return
            }
            logsTab.addLog(System.currentTimeMillis(), "TRACE", p0)
        }

        override fun trace(p0: String?, p1: Any?) {
            throw IllegalArgumentException("trace(p0: String?, p1: Any?)")
        }

        override fun trace(p0: String?, p1: Any?, p2: Any?) {
            throw IllegalArgumentException("trace(p0: String?, p1: Any?, p2: Any?)")
        }

        override fun trace(p0: String?, vararg p1: Any?) {
            throw IllegalArgumentException("trace(p0: String?, vararg p1: Any?)")
        }

        override fun trace(p0: String?, p1: Throwable?) {
            throw IllegalArgumentException("trace(p0: String?, p1: Throwable?)")
        }

        override fun trace(p0: Marker?, p1: String?) {
            throw IllegalArgumentException("trace(p0: Marker?, p1: String?)")
        }

        override fun trace(p0: Marker?, p1: String?, p2: Any?) {
            throw IllegalArgumentException("trace(p0: Marker?, p1: String?, p2: Any?)")
        }

        override fun trace(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {
            throw IllegalArgumentException("trace(p0: Marker?, p1: String?, p2: Any?, p3: Any?)")
        }

        override fun trace(p0: Marker?, p1: String?, vararg p2: Any?) {
            throw IllegalArgumentException("trace(p0: Marker?, p1: String?, vararg p2: Any?)")
        }

        override fun trace(p0: Marker?, p1: String?, p2: Throwable?) {
            throw IllegalArgumentException("trace(p0: Marker?, p1: String?, p2: Throwable?)")
        }

        override fun isDebugEnabled(): Boolean {
            return l.isDebugEnabled
        }

        override fun isDebugEnabled(p0: Marker?): Boolean {
            return l.isDebugEnabled(p0)
        }

        override fun debug(p0: String?) {
            l.debug(p0)

            if (System.getProperty("VQL_TEST_MODE") == "true") {
                val logPattern = formatter.format(Instant.now()) + " DEBUG " + name + " - " + p0 + "\n"
                outputFile?.appendText(logPattern)
                return
            }
            if (project.isDisposed) {
                return
            }
            logsTab.addLog(System.currentTimeMillis(), "DEBUG", p0)
        }

        override fun debug(p0: String?, p1: Any?) {
            throw IllegalArgumentException("debug(p0: String?, p1: Any?)")
        }

        override fun debug(p0: String?, p1: Any?, p2: Any?) {
            throw IllegalArgumentException("debug(p0: String?, p1: Any?, p2: Any?)")
        }

        override fun debug(p0: String?, vararg p1: Any?) {
            throw IllegalArgumentException("debug(p0: String?, vararg p1: Any?)")
        }

        override fun debug(p0: String?, p1: Throwable?) {
            throw IllegalArgumentException("debug(p0: String?, p1: Throwable?)")
        }

        override fun debug(p0: Marker?, p1: String?) {
            throw IllegalArgumentException("debug(p0: Marker?, p1: String?)")
        }

        override fun debug(p0: Marker?, p1: String?, p2: Any?) {
            throw IllegalArgumentException("debug(p0: Marker?, p1: String?, p2: Any?)")
        }

        override fun debug(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {
            throw IllegalArgumentException("debug(p0: Marker?, p1: String?, p2: Any?, p3: Any?)")
        }

        override fun debug(p0: Marker?, p1: String?, vararg p2: Any?) {
            throw IllegalArgumentException("debug(p0: Marker?, p1: String?, vararg p2: Any?)")
        }

        override fun debug(p0: Marker?, p1: String?, p2: Throwable?) {
            throw IllegalArgumentException("debug(p0: Marker?, p1: String?, p2: Throwable?)")
        }

        override fun isInfoEnabled(): Boolean {
            return l.isInfoEnabled
        }

        override fun isInfoEnabled(p0: Marker?): Boolean {
            return l.isInfoEnabled(p0)
        }

        override fun info(p0: String?) {
            l.info(p0)

            if (System.getProperty("VQL_TEST_MODE") == "true") {
                val logPattern = formatter.format(Instant.now()) + " INFO " + name + " - " + p0 + "\n"
                outputFile?.appendText(logPattern)
                return
            }
            if (project.isDisposed) {
                return
            }
            logsTab.addLog(System.currentTimeMillis(), "INFO", p0)
        }

        override fun info(p0: String?, p1: Any?) {
            throw IllegalArgumentException("info(p0: String?, p1: Any?)")
        }

        override fun info(p0: String?, p1: Any?, p2: Any?) {
            throw IllegalArgumentException("info(p0: String?, p1: Any?, p2: Any?)")
        }

        override fun info(p0: String?, vararg p1: Any?) {
            throw IllegalArgumentException("info(p0: String?, vararg p1: Any?)")
        }

        override fun info(p0: String?, p1: Throwable?) {
            throw IllegalArgumentException("info(p0: String?, p1: Throwable?)")
        }

        override fun info(p0: Marker?, p1: String?) {
            throw IllegalArgumentException("info(p0: Marker?, p1: String?)")
        }

        override fun info(p0: Marker?, p1: String?, p2: Any?) {
            throw IllegalArgumentException("info(p0: Marker?, p1: String?, p2: Any?)")
        }

        override fun info(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {
            throw IllegalArgumentException("info(p0: Marker?, p1: String?, p2: Any?, p3: Any?)")
        }

        override fun info(p0: Marker?, p1: String?, vararg p2: Any?) {
            throw IllegalArgumentException("info(p0: Marker?, p1: String?, vararg p2: Any?)")
        }

        override fun info(p0: Marker?, p1: String?, p2: Throwable?) {
            throw IllegalArgumentException("info(p0: Marker?, p1: String?, p2: Throwable?)")
        }

        override fun isWarnEnabled(): Boolean {
            return l.isWarnEnabled
        }

        override fun isWarnEnabled(p0: Marker?): Boolean {
            return l.isWarnEnabled(p0)
        }

        override fun warn(p0: String?) {
            l.warn(p0)

            if (System.getProperty("VQL_TEST_MODE") == "true") {
                val logPattern = formatter.format(Instant.now()) + " WARN " + name + " - " + p0 + "\n"
                outputFile?.appendText(logPattern)
                return
            }
            if (project.isDisposed) {
                return
            }
            logsTab.addLog(System.currentTimeMillis(), "WARN", p0)
        }

        override fun warn(p0: String?, p1: Any?) {
            throw IllegalArgumentException("warn(p0: String?, p1: Any?)")
        }

        override fun warn(p0: String?, vararg p1: Any?) {
            throw IllegalArgumentException("warn(p0: String?, vararg p1: Any?)")
        }

        override fun warn(p0: String?, p1: Any?, p2: Any?) {
            throw IllegalArgumentException("warn(p0: String?, p1: Any?, p2: Any?)")
        }

        override fun warn(p0: String?, p1: Throwable?) {
            l.warn(p0, p1)

            if (System.getProperty("VQL_TEST_MODE") == "true") {
                outputFile?.appendText("$p0\n")
                return
            }
            if (project.isDisposed) {
                return
            }
            logsTab.addLog(System.currentTimeMillis(), "WARN", p0)
        }

        override fun warn(p0: Marker?, p1: String?) {
            throw IllegalArgumentException("warn(p0: Marker?, p1: String?)")
        }

        override fun warn(p0: Marker?, p1: String?, p2: Any?) {
            throw IllegalArgumentException("warn(p0: Marker?, p1: String?, p2: Any?)")
        }

        override fun warn(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {
            throw IllegalArgumentException("warn(p0: Marker?, p1: String?, p2: Any?, p3: Any?)")
        }

        override fun warn(p0: Marker?, p1: String?, vararg p2: Any?) {
            throw IllegalArgumentException("warn(p0: Marker?, p1: String?, vararg p2: Any?)")
        }

        override fun warn(p0: Marker?, p1: String?, p2: Throwable?) {
            throw IllegalArgumentException("warn(p0: Marker?, p1: String?, p2: Throwable?)")
        }

        override fun isErrorEnabled(): Boolean {
            return l.isErrorEnabled
        }

        override fun isErrorEnabled(p0: Marker?): Boolean {
            return l.isErrorEnabled(p0)
        }

        override fun error(p0: String?) {
            l.error(p0)

            if (System.getProperty("VQL_TEST_MODE") == "true") {
                val logPattern = formatter.format(Instant.now()) + " ERROR " + name + " - " + p0 + "\n"
                outputFile?.appendText(logPattern)
                return
            }
            if (project.isDisposed) {
                return
            }
            logsTab.addLog(System.currentTimeMillis(), "ERROR", p0)
        }

        override fun error(p0: String?, p1: Any?) {
            throw IllegalArgumentException("error(p0: String?, p1: Any?)")
        }

        override fun error(p0: String?, p1: Any?, p2: Any?) {
            throw IllegalArgumentException("error(p0: String?, p1: Any?, p2: Any?)")
        }

        override fun error(p0: String?, vararg p1: Any?) {
            throw IllegalArgumentException("error(p0: String?, vararg p1: Any?)")
        }

        override fun error(p0: String?, p1: Throwable?) {
            l.error(p0, p1)

            if (System.getProperty("VQL_TEST_MODE") == "true") {
                val logPattern = formatter.format(Instant.now()) + " ERROR " + name + " - " + p0 + "\n" +
                        p1?.stackTraceToString()
                outputFile?.appendText(logPattern)
                return
            }
            if (project.isDisposed) {
                return
            }
            logsTab.addLog(System.currentTimeMillis(), "ERROR", p0)
        }

        override fun error(p0: Marker?, p1: String?) {
            throw IllegalArgumentException("error(p0: Marker?, p1: String?)")
        }

        override fun error(p0: Marker?, p1: String?, p2: Any?) {
            throw IllegalArgumentException("error(p0: Marker?, p1: String?, p2: Any?)")
        }

        override fun error(p0: Marker?, p1: String?, p2: Any?, p3: Any?) {
            throw IllegalArgumentException("error(p0: Marker?, p1: String?, p2: Any?, p3: Any?)")
        }

        override fun error(p0: Marker?, p1: String?, vararg p2: Any?) {
            throw IllegalArgumentException("error(p0: Marker?, p1: String?, vararg p2: Any?)")
        }

        override fun error(p0: Marker?, p1: String?, p2: Throwable?) {
            throw IllegalArgumentException("error(p0: Marker?, p1: String?, p2: Throwable?)")
        }
    }
}
