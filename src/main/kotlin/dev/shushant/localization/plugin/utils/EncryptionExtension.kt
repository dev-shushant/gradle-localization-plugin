package dev.shushant.localization.plugin.utils

/**
 * created by Shushant Tiwari
 * This class holds configuration settings for string encryption within an Android project.
 *
 *  It defines key-value pairs of strings to be encrypted and specifies output file
 *  and properties file for the encryption process.
 *
 *  **Key Features:**
 *
 *  *   `outputFile`:  Specifies the path to the C++ header file where encrypted strings
 *      will be stored.  Defaults to "src/main/cpp/encrypted_strings.h".
 *  *   `keystorePropertiesFile`:  Indicates the location of the `local.properties`
 *      file, which should contain the keystore password for encryption/decryption.
 *      Defaults to "local.properties".
 *  *   `secrets`:  A `Map` containing the strings to be encrypted. Keys describe the
 *      string's purpose (e.g., "baseUrlEncrypted_DEV", "razorpayKeyEncrypted_PROD"),
 *      and values are the actual strings to be encrypted. Note that the key names should
 *      be descriptive as they are used as variable names in the C++ header file.
 *
 *  **Usage:**
 *
 *  This extension is typically used within a Gradle build script to configure the
 *  string encryption process during the build.  For example:
 *
 *  ```kotlin
 *  encryption {
 *      outputFile = "src/main/cpp/secrets.h"
 *      keystorePropertiesFile = "secrets.properties"
 *      secrets = mapOf(
 *          "apiKeyEncrypted" to "your_api_key",
 *          "privateKeyEncrypted" to "your_private_key"
 *      )
 *  }
 *  ```
 *
 *  **Note:**  Ensure that your `local.properties` (or the file specified by
 *  `keystorePropertiesFile`) contains the line `keystorePassword=your_password`
 *  where `your_password` is the password you used when setting up the keystore.
 */
open class EncryptionExtension {
    var outputFile: String = "src/main/cpp/secrets.h"
    var keystorePropertiesFile: String = "local.properties"
    val secrets: Map<String, String> = emptyMap()
    var generateNativeCpp: Boolean = false
    var cppPackageName: String = ""
    internal var outputDir: String = "src/main/cpp"
}
