package dev.shushant.localization.plugin.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Request
import com.google.gson.JsonParser

class LibreTranslator(private val client: okhttp3.OkHttpClient) : TranslatorService {

    override suspend fun translate(text: String, sourceLang: String, targetLang: String): String? = withContext(Dispatchers.IO) {
        try {
            val body = FormBody.Builder()
                .add("q", text)
                .add("source", sourceLang)
                .add("target", targetLang)
                .build()

            val request = Request.Builder()
                .url("https://libretranslate.de/translate")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val bodyString = response.body?.string()
            val jsonObject = JsonParser.parseString(bodyString).asJsonObject
            return@withContext DependencyHelper.sanitize(jsonObject["translatedText"]?.asString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}