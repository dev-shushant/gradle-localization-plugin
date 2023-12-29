package dev.shushant.localization.plugin

import dev.shushant.localization.plugin.utils.GenerateTranslations
import dev.shushant.localization.plugin.utils.Languages
import dev.shushant.localization.plugin.utils.Translator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File


abstract class LocalizationTask : DefaultTask() {
    @get:Input
    var supportedLang = listOf<Languages>()

    @get:Input
    var flavourType: String = ""

    @get:Input
    var moduleName: String = ""

    @get:Input
    var packageName: String = ""

    @get:Input
    var pathToGenerateSupportedLanguageEnum: String = ""

    @TaskAction
    fun doTranslate() {
        val designSystemModule = if(moduleName.isEmpty()) project else project.project(moduleName)
        val path = designSystemModule.layout.projectDirectory.toString()
        val originalFile = File(path)
        val translationBuilder = GenerateTranslations
            .Builder(originalFile)
            .build()
        if (translationBuilder.isModified()) {
            translationBuilder.saveCurrentHash()
            val listOfStrings = translationBuilder.listElements()
            val translator = Translator.Builder()
                .addNodes(listOfStrings)
                .build()
            supportedLang.forEach { lang ->
                println("Generating localization for: ${lang.code.uppercase()} $flavourType")
                val translated = translator.translate(lang.code)
                translationBuilder.saveLocalized(lang.code, translated)
            }
        }
        generateLanguageEnum()
    }

    private fun generateLanguageEnum() {
        pathToGenerateSupportedLanguageEnum.takeIf { it.isNotEmpty() && packageName.isNotEmpty() }?.let {
            moduleName.takeIf { it.isNotEmpty() }
            val designSystemModule = if (moduleName.isNotEmpty()) project.project(moduleName) else project
            val outputDir =
                designSystemModule.projectDir.resolve(pathToGenerateSupportedLanguageEnum)
            outputDir.mkdirs()

            val outputFile = File(outputDir, "Language.kt")
            outputFile.writeText(generateEnumContent())
        } ?: run {
            logger.lifecycle("package name and pathToGenerateSupportedLanguageEnum is must to generate the Language Enum ")
        }
    }

    private fun generateEnumContent(): String {
        val enumBuilder = StringBuilder()
        enumBuilder.append("package ${packageName}\n\n")
        enumBuilder.append("enum class Language(val code : String) {\n")

        supportedLang.forEach { lang ->
            enumBuilder.append("    ${lang.name}(\"${lang.code}\"),\n")
        }
        enumBuilder.append("}\n")
        return enumBuilder.toString()
    }
}