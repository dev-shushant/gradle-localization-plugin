package dev.shushant.localization.plugin.utils

import java.io.File

object NativeCppGenerator {

    fun generate(
        outputDir: File,
        headerFileName: String,
        cppPackageName: String
    ) {
        val headerFile = File(outputDir, headerFileName)
        if (!headerFile.exists()) return

        val lines = headerFile.readLines()
        val macroMap = mutableMapOf<String, MutableList<Pair<String, String>>>()
        val staticMacros = mutableListOf<Pair<String, String>>()

        val flavorPattern = Regex("([a-zA-Z0-9]+)Enc_([A-Z_]+)")

        lines.filter { it.trim().startsWith("#define") }
            .forEach { line ->
                val parts = line.trim().split(Regex("\\s+"))
                if (parts.size >= 3) {
                    val macro = parts[1]
                    val secretKey = parts[2].trim()
                    flavorPattern.matchEntire(macro)?.let { match ->
                        val key = match.groupValues[1]
                        val flavor = match.groupValues[2]
                        macroMap.getOrPut(key) { mutableListOf() }.add(flavor to macro)
                    } ?: staticMacros.add(secretKey to macro)
                }
            }

        val jniClass = cppPackageName.replace('.', '_')
        val nativeCpp = StringBuilder()

        nativeCpp.appendLine("#include <jni.h>")
        nativeCpp.appendLine("#include <string>")
        nativeCpp.appendLine("#include <unordered_map>")
        nativeCpp.appendLine("#include \"$headerFileName\"")
        nativeCpp.appendLine()
        nativeCpp.appendLine("// Flavor-based map")
        nativeCpp.appendLine("std::string getFlavorDecryptedValue(const std::string &flavor, const std::unordered_map<std::string, std::string> &flavorMap) {")
        nativeCpp.appendLine("    auto it = flavorMap.find(flavor);")
        nativeCpp.appendLine("    if (it != flavorMap.end()) return dt(it->second);")
        nativeCpp.appendLine("    return dt(flavorMap.at(\"PROD\")); // fallback")
        nativeCpp.appendLine("}")
        nativeCpp.appendLine()

        // Generate maps
        macroMap.forEach { (key, flavors) ->
            nativeCpp.appendLine("static std::unordered_map<std::string, std::string> ${key}Map = {")
            flavors.forEach { (flavor, macro) ->
                nativeCpp.appendLine("    {\"$flavor\", $macro},")
            }
            nativeCpp.appendLine("};")
            nativeCpp.appendLine()
        }

        // JNI methods for flavor-based maps
        macroMap.forEach { (key, _) ->
            nativeCpp.appendLine("extern \"C\" JNIEXPORT jstring JNICALL")
            nativeCpp.appendLine("Java_${jniClass}_common_Keys_${key}(JNIEnv *env, jobject, jstring flavor) {")
            nativeCpp.appendLine("    const char *flav = env->GetStringUTFChars(flavor, nullptr);")
            nativeCpp.appendLine("    std::string result = getFlavorDecryptedValue(flav, ${key}Map);")
            nativeCpp.appendLine("    env->ReleaseStringUTFChars(flavor, flav);")
            nativeCpp.appendLine("    return env->NewStringUTF(result.c_str());")
            nativeCpp.appendLine("}")
            nativeCpp.appendLine()
        }

        // Static string macros
        nativeCpp.appendLine("#define RETURN_STATIC_STRING(funcName, encValue) \\")
        nativeCpp.appendLine("    extern \"C\" JNIEXPORT jstring JNICALL \\")
        nativeCpp.appendLine("    Java_${jniClass}_common_Keys_##funcName(JNIEnv *env, jobject) { \\")
        nativeCpp.appendLine("        return env->NewStringUTF(dt(encValue).c_str()); \\")
        nativeCpp.appendLine("    }")
        nativeCpp.appendLine()

        staticMacros.forEach { (funcName, macro) ->
            nativeCpp.appendLine("RETURN_STATIC_STRING(${funcName}, ${macro})")
        }

        val file = File(outputDir, "native-lib.cpp")
        file.writeText(nativeCpp.toString())
    }
}


