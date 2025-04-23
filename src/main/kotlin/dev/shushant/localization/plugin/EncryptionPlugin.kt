package dev.shushant.localization.plugin

/**
 * A Gradle plugin for encrypting sensitive strings and generating a C++ header file containing the encrypted strings.
 *
 * This plugin adds a task `encryptStrings` to the project, which performs the encryption and header generation.
 * It also configures the `preBuild` task to depend on the `encryptStrings` task, ensuring that secrets are
 * encrypted before each build.
 *
 * The plugin uses an extension `encryptionConfig` to allow customization of encryption parameters.
 *
 * Usage:
 * Apply the plugin to your project and configure the `encryptionConfig` extension in your `build.gradle.kts` file.
 * Created by Shushant Tiwari
 */
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.shushant.localization.plugin.utils.EncryptionExtension
import dev.shushant.localization.plugin.utils.HeaderFileGenerator
import dev.shushant.localization.plugin.utils.RsaKeyLoader
import okhttp3.OkHttpClient
import okhttp3.Request
import org.gradle.api.Plugin
import org.gradle.api.Project

class EncryptionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension =
            project.extensions.create("encryptionConfig", EncryptionExtension::class.java)

        val encryption = project.tasks.register("encryptStrings") {
            group = "encryption"
            description = "Encrypts secrets and generates C++ header"

            doLast {
                val secrets = fetchSecrets(extension.secretsUrl, extension.secrets)

                if (secrets.isEmpty()) {
                    throw IllegalStateException("No secrets provided. Either 'secretUrl' or 'secrets' map must be set.")
                }

                val key = RsaKeyLoader.load(
                    project = project, propertiesFilePath = extension.keystorePropertiesFile
                )

                HeaderFileGenerator.generate(
                    project = project,
                    outputPath = extension.outputFile,
                    key = key,
                    secrets = secrets,
                    generateCppFile = extension.generateNativeCpp,
                    cppPath = extension.outputDir,
                    packageName = extension.cppPackageName,
                )

                println("Generated encrypted strings header.")
            }

            notCompatibleWithConfigurationCache("Header generation not yet cache-safe.")
        }

        project.afterEvaluate {
            project.tasks.findByName("preBuild")?.dependsOn(encryption)
        }
    }

    private fun fetchSecrets(
        secretUrl: String?, fallbackSecrets: Map<String, String>
    ): Map<String, String> {
        return try {
            if (!secretUrl.isNullOrBlank()) {
                val client = OkHttpClient()
                val request = Request.Builder().url(secretUrl).build()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    println("[EncryptionPlugin] Failed to fetch secrets: HTTP ${response.code}. Falling back.")
                    return fallbackSecrets
                }

                val json = response.body?.string() ?: ""
                val type = object : TypeToken<Map<String, String>>() {}.type
                Gson().fromJson<Map<String, String>>(json, type)
            } else {
                fallbackSecrets
            }
        } catch (e: Exception) {
            println("[EncryptionPlugin] Error fetching secrets from URL: ${e.message}. Falling back to local secrets.")
            fallbackSecrets
        }
    }
}


