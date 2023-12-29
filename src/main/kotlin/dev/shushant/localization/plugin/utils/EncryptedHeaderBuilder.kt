package dev.shushant.localization.plugin.utils

import java.util.Base64

/**
 * This class constructs an encrypted header file in C++ that can be used to store sensitive string data securely within an application.
 * The header includes definitions for accessing the encrypted data and functions to decrypt it at runtime.
 *
 * @property key A secret key used for encrypting and decrypting the strings. This key should be a strong, randomly generated string.
 * @property secrets A map of key-value pairs where each key is a constant name that will be used in the generated header file,
 *                   and the value is the secret string that needs to be encrypted.
 *
 * Usage:
 *  1. Create an instance of `EncryptedHeaderBuilder` with a strong key and a map of your secrets.
 * created by Shushant tiwari
 */
class EncryptedHeaderBuilder(
    private val key: String,
    private val secrets: Map<String, String>
) {

    private val obfuscatedFunc = generateRandomName()
    private val obfuscatedVar = generateRandomName()

    /**
     * Builds the encrypted C++ header file as a string.
     *
     * The header file includes:
     * 1. Encrypted string constants defined using `#define`.
     * 2. A function to retrieve the secret key (obfuscated).
     * 3. Decryption logic for decrypting the encrypted strings at runtime.
     */
    fun build(): String {
        val lines = mutableListOf(
            "// Auto-generated. Do not edit manually.",
            "#ifndef ENCRYPTED_STRINGS_H",
            "#define ENCRYPTED_STRINGS_H",
            "#include <string>",
            "#include <vector>",
            "#include <stdexcept>",
            ""
        )

        addEncryptedConstants(lines)
        addSecretKeyFunction(lines)
        addDecryptionLogic(lines)

        lines += "#endif // ENCRYPTED_STRINGS_H"
        return lines.joinToString("\n")
    }

    /**
     * Adds encrypted constants to the list of header lines.
     *
     * Each secret is encrypted using the provided key and then Base64 encoded. The result is stored as a `#define` constant in the header.
     */
    private fun addEncryptedConstants(lines: MutableList<String>) {
        secrets.forEach { (name, value) ->
            val encrypted = xE(value, key)
            val base64 = Base64.getEncoder().encodeToString(encrypted.toByteArray())
            lines += """#define $name "$base64""""
        }
        lines += ""
    }

    /**
     * Adds a function to retrieve the secret key to the header.
     *
     * This function returns the secret key used for encryption/decryption. The function name and an internal variable name are obfuscated
     * to make reverse engineering slightly more difficult.
     */
    private fun addSecretKeyFunction(lines: MutableList<String>) {
        lines += """
std::string $obfuscatedFunc() {
    std::string $obfuscatedVar = "$key";
    return $obfuscatedVar;
}
""".trimIndent()
    }

    /**
     * Adds the decryption logic to the header file. This includes two functions:
     * - `bd`: Performs Base64 decoding.
     * - `dt`: Decrypts the input string using a simple XOR cipher with the secret key.
     *
     * The `dt` function first Base64 decodes the input, then decrypts it by XORing each character with the corresponding character from the secret key, repeating the key as necessary.
     *
     * Usage Example:
     */
    private fun addDecryptionLogic(lines: MutableList<String>) {
        lines += """
std::string bd(const std::string &in) {
    std::string out;
    std::vector<int> T(256, -1);
    for (int i = 0; i < 64; i++) {
        T["ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="[i]] = i;
    }
    int val = 0, valb = -8;
    for (unsigned char c : in) {
        if (T[c] == -1) break;
        val = (val << 6) + T[c];
        valb += 6;
        if (valb >= 0) {
            out.push_back(char((val >> valb) & 0xFF));
            valb -= 8;
        }
    }
    return out;
}

std::string dt(const std::string &base64Encoded) {
    std::string decoded = bd(base64Encoded);
    std::string key = $obfuscatedFunc();
    std::string decrypted;

    for (size_t i = 0; i < decoded.size(); ++i) {
        decrypted += decoded[i] ^ key[i % key.length()];
    }

    return decrypted;
}
""".trimIndent()
    }

    /**
     * Performs a simple XOR encryption/decryption.
     *
     * This function encrypts or decrypts the input string by XORing each character's ASCII code with the ASCII code of the character at the corresponding position in the key.
     * If the input string is longer than the key, the key is repeated.
     * @param input The string to be encrypted or decrypted.
     * @param key The encryption/decryption key.
     */
    private fun xE(input: String, key: String): String =
        input.mapIndexed { i, c -> c.code.xor(key[i % key.length].code).toChar() }.joinToString("")
}
