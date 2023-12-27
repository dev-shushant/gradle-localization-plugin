package dev.shushant.localization.plugin

import dev.shushant.localization.plugin.utils.DESIGN_SYSTEM
import dev.shushant.localization.plugin.utils.GenerateTranslations
import dev.shushant.localization.plugin.utils.STRINGS_XML
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Document
import java.io.File
import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory

abstract class GenerateAppResourcesTask : DefaultTask() {

    @OutputFile
    lateinit var outputClassFileAppString: File

    @OutputFile
    lateinit var outputIconClassFile: File

    @get:Input
    var packageName = ""

    @get:Input
    var classNameForStrings = "AppStrings"

    @get:Input
    var classNameForIcons = "AppIcons"

    @get:Input
    var applicationId = ""

    @TaskAction
    fun generate() {
        createDirectories()
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val designSystemModule = project
        val mProjDir = File(designSystemModule.layout.projectDirectory.toString())
        val inputFile = File("$mProjDir${GenerateTranslations.PATH_VALUES}${STRINGS_XML}")
        val document = docBuilder.parse(FileInputStream(inputFile))

        val properties = generateProperties(document)
        val outputFileContent = """
            package $packageName
            import $applicationId.R
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.ReadOnlyComposable
            import androidx.compose.ui.res.stringResource

            object $classNameForStrings {
                $properties
            }
        """.trimIndent()

        Files.write(
            outputClassFileAppString.toPath(),
            outputFileContent.toByteArray(StandardCharsets.UTF_8),
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )

        generateIconsFile()

    }

    private fun generateIconsFile() {
        // Generate drawable resources
        val designSystemModule = project
        val mProjDir = File(designSystemModule.layout.projectDirectory.toString())
        val drawableDir = File(mProjDir.toString(), "src/main/res/drawable")
        val properties = generateIconProperties(drawableDir)
        val outputFileContent = """
            package $packageName
             import $applicationId.R
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.graphics.painter.Painter
            import androidx.compose.ui.res.painterResource

            object $classNameForIcons {
                $properties
            }
        """.trimIndent()

        Files.write(
            outputIconClassFile.toPath(),
            outputFileContent.toByteArray(StandardCharsets.UTF_8),
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )
    }

    private fun generateIconProperties(drawableDir: File): String {
        var properties = ""
        properties += drawableDir.listFiles()?.mapNotNull { file ->
            val propertyName =
                file.nameWithoutExtension.split('_').joinToString("") { it ->
                    it.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    }
                }
            val name =
                if (file.nameWithoutExtension.startsWith("_")) "_$propertyName" else propertyName
            """
                /**
                 * Gets the value of the [$propertyName] drawable resource.
                 */
                val $name: Painter
                    @Composable
                    get() = painterResource(R.drawable.${file.nameWithoutExtension})
                    """
        }?.joinToString(separator = "\n") ?: ""
        return properties
    }


    private fun createDirectories() {
        val parentDir = outputClassFileAppString.parentFile
        if (!parentDir.exists()) {
            parentDir.mkdirs()
        }
        val parentDirIcons = outputIconClassFile.parentFile
        if (!parentDirIcons.exists()) {
            parentDirIcons.mkdirs()
        }
    }

    private fun generateProperties(document: Document): String {
        var properties = ""

        val nodeList = document.getElementsByTagName("string")
        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i)
            val nodeValue = node.attributes.getNamedItem("name").nodeValue
            val propertyName =
                nodeValue.split('_').joinToString("") { it ->
                    it.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    }
                }

            val name = if (nodeValue.startsWith("_")) "_$propertyName" else propertyName
            properties += """
                /**
                 * Gets the value of the [$propertyName] string resource.
                 */
                val $name: String
                    @ReadOnlyComposable
                    @Composable
                    get() = stringResource(R.string.$nodeValue)

            """
        }

        return properties
    }
}
