package dev.shushant.localization.plugin.utils

import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore
import java.security.MessageDigest
import java.security.PrivateKey
import java.util.Properties
/**
 * Utility object for loading an RSA private key from a keystore and calculating its SHA-256 fingerprint.
 *
 * This object provides a method to load an RSA private key from a keystore specified in a properties file
 * and calculate its SHA-256 fingerprint. The properties file should contain the necessary configuration
 * for accessing the keystore.
 * created by Shushant Tiwari
 */

object RsaKeyLoader {
    /** Loads an RSA private key from a keystore specified in a properties file and returns its SHA-256 fingerprint.
     *
     * This function accesses a keystore file to retrieve an RSA private key and calculate its SHA-256 fingerprint.
     * The keystore details are specified in a properties file, which is loaded from the root project directory.
     *
     * @param project The Gradle project to provide context for file access.
     * @param propertiesFilePath The path to the properties file containing the keystore configuration.
     *                           This path is relative to the root project directory.
     *                           The properties file should contain the following keys:
     *                           - `signing.storeFile`: Path to the keystore file (JKS format). This path is relative to the root project directory.
     *                           - `signing.storePassword`: Password for the keystore.
     *                           - `signing.keyAlias`: Alias of the private key within the keystore.
     *                           - `signing.keyPassword`: Password for the private key.
     * @return The SHA-256 fingerprint of the RSA private key as a hexadecimal string. */
    fun load(project: Project, propertiesFilePath: String): String {
        val props = Properties().apply {
            val file = project.rootProject.file(propertiesFilePath)
            if (!file.exists()) error("Missing $propertiesFilePath")
            load(file.inputStream())
        }

        val keystore = KeyStore.getInstance("JKS")
        keystore.load(FileInputStream(File(props["signing.storeFile"].toString())),
                      props["signing.storePassword"].toString().toCharArray())

        val key = keystore.getKey(
            props["signing.keyAlias"].toString(),
            props["signing.keyPassword"].toString().toCharArray()
        ) as? PrivateKey ?: error("Key alias is not a PrivateKey")

        val digest = MessageDigest.getInstance("SHA-256").digest(key.encoded)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
