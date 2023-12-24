package dev.shushant.localization.plugin

import dev.shushant.localization.plugin.utils.APP_RESOURCES_EXTENSION
import dev.shushant.localization.plugin.utils.GENERATE_APP_STRING_TASK
import dev.shushant.localization.plugin.utils.PRE_BUILD_TASK
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class GenerateAppResourceClassPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension =
            project.extensions.create(APP_RESOURCES_EXTENSION, AppResourceExtension::class.java)

        val task =
            project.tasks.register(GENERATE_APP_STRING_TASK, GenerateAppResourcesTask::class.java) {
                outputClassFileAppString =
                    File(
                        if (extension.moduleName.isNotEmpty()) project.project(extension.moduleName).projectDir else project.projectDir,
                        extension.stringFilePathWithFileName
                    )
                outputIconClassFile =
                    File(
                        if (extension.moduleName.isNotEmpty()) project.project(extension.moduleName).projectDir else project.projectDir,
                        extension.iconsFilePathWithFileName
                    )
                packageName = extension.packageNameWhereToGenerateFiles
                applicationId = extension.applicationId
                classNameForIcons = extension.classNameForIcons
                classNameForStrings = extension.classNameForStrings
            }
        project.tasks.named(PRE_BUILD_TASK).configure {
            dependsOn(task)
        }
    }
}