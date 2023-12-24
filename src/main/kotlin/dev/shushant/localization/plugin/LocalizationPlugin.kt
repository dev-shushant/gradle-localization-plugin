package dev.shushant.localization.plugin

import dev.shushant.localization.plugin.utils.GENERATE_TRANSLATION_TASK
import dev.shushant.localization.plugin.utils.LOCALIZATION_EXTENSION
import dev.shushant.localization.plugin.utils.PRE_BUILD_TASK
import org.gradle.api.Plugin
import org.gradle.api.Project

class LocalizationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension =
            project.extensions.create(LOCALIZATION_EXTENSION, LocalizationExtension::class.java)
        val task =
            project.tasks.register(GENERATE_TRANSLATION_TASK, LocalizationTask::class.java) {
                supportedLang = extension.supportedLang
                moduleName = extension.moduleName
                pathToGenerateSupportedLanguageEnum = extension.pathToGenerateSupportedLanguageEnum
            }
        project.tasks.named(PRE_BUILD_TASK).get().dependsOn(task)
    }
}