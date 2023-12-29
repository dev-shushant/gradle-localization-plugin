package dev.shushant.localization.plugin.utils

import org.gradle.api.Project

/**
 * Utility object for generating and writing encrypted header files.
 * created by Shushant Tiwari
 */
object HeaderFileGenerator {

    /**
     * Generates an encrypted header file.
     * @param project The Gradle project context. Used to resolve file paths.
     * @param outputPath The path where the header file should be written, relative to the project's directory.
     * @param key The encryption key used to encrypt the secrets.
     * @param secrets A map of secret names to their corresponding values.  These will be encrypted and included in the header file.
     */
    fun generate(
        project: Project,
        outputPath: String,
        key: String,
        secrets: Map<String, String>,
        generateCppFile: Boolean,
        cppPath: String,
        packageName: String
    ) {
        val headerBuilder = EncryptedHeaderBuilder(key, secrets)
        val outputFile = project.file(outputPath)
        val generatedContent = headerBuilder.build()

        if (!outputFile.exists() || outputFile.readText() != generatedContent) {
            outputFile.writeText(generatedContent)
        }
        if (generateCppFile){
            NativeCppGenerator.generate(
                outputDir = project.file(cppPath),
                headerFileName = outputFile.name,
                cppPackageName = packageName
            )
        }
    }
}


/**
 * Generates a random alphanumeric string.
 * @param length The desired length of the random string. Defaults to 12.
 * @return A random string of the specified length.
 */
fun generateRandomName(length: Int = 12): String {
    val allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    return (1..length).map { allowedChars.random() }.joinToString("")
}
