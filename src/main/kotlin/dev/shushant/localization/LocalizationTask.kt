package dev.shushant.localization

import dev.shushant.localization.utils.GenerateTranslations
import dev.shushant.localization.utils.Languages
import dev.shushant.localization.utils.Translator
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

    @TaskAction
    fun doTranslate() {
        val designSystemModule = project.project(moduleName)
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
        val designSystemModule = project.project(moduleName)
        val outputDir =
            designSystemModule.projectDir.resolve("src/main/kotlin/com/msil/design_system/utils/language")
        outputDir.mkdirs()

        val outputFile = File(outputDir, "Language.kt")
        outputFile.writeText(generateEnumContent())
    }

    private fun generateEnumContent(): String {
        val enumBuilder = StringBuilder()
        enumBuilder.append("package com.msil.design_system.utils.language\n\n")
        enumBuilder.append("enum class Language(val code : String) {\n")

        supportedLang.forEach { lang ->
            enumBuilder.append("    ${lang.name}(\"${lang.code}\"),\n")
        }
        enumBuilder.append("}\n")
        return enumBuilder.toString()
    }
}