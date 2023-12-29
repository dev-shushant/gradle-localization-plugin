package  dev.shushant.localization.plugin.utils

import com.google.gson.JsonParser
import dev.shushant.localization.plugin.models.LocalizationNode
import okhttp3.OkHttpClient
import okhttp3.Request

class Translator private constructor(builder: Builder) {
    private val nodes: List<LocalizationNode> = builder.nodes

    class Builder {
        val nodes = mutableListOf<LocalizationNode>()
        fun addNodes(nodeList: List<LocalizationNode>): Builder {
            nodes.addAll(nodeList)
            return this
        }

        fun build(): Translator = Translator(this)
    }

    fun translate(lang: String): List<LocalizationNode> {
        val translatedNodes = mutableListOf<LocalizationNode>()
        for (node in nodes) {
            val translatedValue = DependencyHelper.getTranslation(lang, node.value)
            translatedNodes.add(LocalizationNode(node.name, translatedValue ?: node.value))
        }
        return translatedNodes
    }
}


object DependencyHelper {
    private val client = OkHttpClient()

    fun getTranslation(lang: String, text: String): String? {
        val urlString =
            "$TRANSLATE_BASE_URL$lang&q=$text"
        val request = Request.Builder()
            .url(urlString)
            .build()
        try {
            val response = client.newCall(request).execute()
            val jsonResponse = response.body?.string()

            // Parse JSON using Gson JsonParser
            val jsonArray = JsonParser.parseString(jsonResponse).asJsonArray
            return sanitize(jsonArray.firstOrNull()?.asString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun sanitize(input: String?): String? {
        return input?.replace("&", "&amp;")
            ?.replace("<", " &lt;")
            ?.replace(">", " &gt;")
            ?.replace("\"", " &quot;")
            ?.replace("'", " &apos;")
            ?.replace("\u0022", " &quot;") // Unicode escape for double quote
            ?.replace("\u0027", " &apos;") // Unicode escape for single quote
    }
}