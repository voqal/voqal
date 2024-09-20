package benchmark

import java.io.File

fun main() {
    val results = File("benchmark", "results.jsonl")
    File("benchmark").walk().forEach { file ->
        if (file.isFile && file.name.endsWith(".json")) {
            results.appendText(file.readText() + "\n")
        }
    }
}
