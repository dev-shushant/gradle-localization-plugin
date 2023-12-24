package dev.shushant.localization.utils

import org.gradle.api.invocation.Gradle

const val GENERATE_TRANSLATION_TASK = "generateLocalizationTask"
const val GENERATE_APP_STRING_TASK = "generateAppResourceTask"
const val FETCH_CONFIG_TASK = "fetchConfigTask"
const val LOCALIZATION_EXTENSION = "localization"
const val PRE_BUILD_TASK = "preBuild"
const val ASSEMBLE = "assemble"
const val STRINGS_XML = "strings.xml"
const val STRING_ELEMENT = "string"
const val DESIGN_SYSTEM = ":design_system"
const val RESOURCES = "resources"
const val TRANSLATE_BASE_URL =
    "https://translate.googleapis.com/translate_a/t?client=gtx&dt=t&sl=en&tl="
const val SUZUKI_STRING_FILE =
    "src/main/kotlin/com/msil/design_system/utils/SuzukiConnectAppStrings.kt"

const val SUZUKI_CONFIG_FILE =
    "src/main/kotlin/com/msil/design_system/config/SuzukiConnectAppConfig.kt"
const val SUZUKI_APP_ICON_FILE =
    "src/main/kotlin/com/msil/design_system/utils/SuzukiConnectAppIcons.kt"

fun getCurrentFlavor(gradle: Gradle): String? {
    gradle.startParameter.taskRequests.forEach {
        it.args.forEach { arg ->
            if (arg.contains("assemble") || arg.contains("generate")) {
                return arg
            }
        }
    }
    return null
}