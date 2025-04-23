package  dev.shushant.localization.plugin.utils

import dev.shushant.localization.plugin.models.LocalizationNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient

class Translator private constructor(
    private val nodes: List<LocalizationNode>,
    private val services: List<TranslatorService>
) {
    class Builder {
        private var nodes: List<LocalizationNode> = emptyList()
        private val services: MutableList<TranslatorService> = mutableListOf()

        fun addNodes(nodes: List<LocalizationNode>) = apply {
            this.nodes = nodes
        }

        fun addService(service: TranslatorService) = apply {
            services.add(service)
        }

        fun build(): Translator {
            return Translator(nodes, services)
        }
    }

    suspend fun translate(targetLang: String): List<LocalizationNode> = coroutineScope {
        nodes.map { node ->
            async(Dispatchers.IO) {
                translateNode(node, targetLang)
            }
        }.awaitAll()
    }

    private suspend fun translateNode(
        node: LocalizationNode,
        targetLang: String
    ): LocalizationNode {
        services.forEach { service ->
            val translation = service.translate(node.cleanValue, sourceLang = "en", targetLang)
            if (!translation.isNullOrBlank()) {
                val restored = restorePlaceholders(translation, node.placeholders)
                return node.copy(value = restored)
            }
        }
        return node
    }

    fun restorePlaceholders(translatedText: String, placeholders: Map<String, String>): String {
        var result = translatedText
        placeholders.forEach { (token, originalPlaceholder) ->
            result = result.replace(token, originalPlaceholder)
        }
        return result
    }
}

object DependencyHelper {
    val client = OkHttpClient.Builder()
        .callTimeout(10_000, java.util.concurrent.TimeUnit.MILLISECONDS)
        .build()

    fun sanitize(input: String?): String? {
        return input?.replace("&", "&amp;")
            ?.replace("<", "&lt;")
            ?.replace(">", "&gt;")
            ?.replace("\"", "&quot;")
            ?.replace("'", "&apos;")
            ?.replace("\u0022", "&quot;")
            ?.replace("\u0027", "&apos;")
    }
}