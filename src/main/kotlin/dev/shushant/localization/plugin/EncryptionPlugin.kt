package dev.shushant.localization.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import dev.shushant.localization.plugin.utils.EncryptionExtension
import dev.shushant.localization.plugin.utils.HeaderFileGenerator
import dev.shushant.localization.plugin.utils.RsaKeyLoader

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
class EncryptionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension =
            project.extensions.create("encryptionConfig", EncryptionExtension::class.java)


        val encryption = project.tasks.register("encryptStrings") {
            group = "encryption"
            description = "Encrypts secrets and generates C++ header"

            doLast {
                val key = RsaKeyLoader.load(
                    project = project,
                    propertiesFilePath = extension.keystorePropertiesFile
                )
                HeaderFileGenerator.generate(
                    project = project,
                    outputPath = extension.outputFile,
                    key = key,
                    secrets = extension.secrets,
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
}


