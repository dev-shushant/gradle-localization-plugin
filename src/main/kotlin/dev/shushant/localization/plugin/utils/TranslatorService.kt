package dev.shushant.localization.plugin.utils

import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request


interface TranslatorService {
    suspend fun translate(text: String, sourceLang: String, targetLang: String): String?
}


class GoogleTranslator(private val client: okhttp3.OkHttpClient) : TranslatorService {

    override suspend fun translate(text: String, sourceLang: String, targetLang: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val url =
                    "https://translate.googleapis.com/translate_a/single?client=gtx&dt=t&sl=$sourceLang&tl=$targetLang&q=${text.encode()}"

                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val bodyString = response.body?.string()
                val jsonArray = JsonParser.parseString(bodyString).asJsonArray

                val translatedText = buildString {
                    val translations = jsonArray[0].asJsonArray
                    for (translation in translations) {
                        append(translation.asJsonArray[0].asString)
                    }
                }
                return@withContext DependencyHelper.sanitize(translatedText)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    private fun String.encode(): String =
        java.net.URLEncoder.encode(this, Charsets.UTF_8.name())
}