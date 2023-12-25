package dev.shushant.localization.plugin

import dev.shushant.localization.plugin.utils.Languages


open class LocalizationExtension {
    var supportedLang = listOf<Languages>()
    var moduleName: String = ""
    var packageName: String = ""
    var pathToGenerateSupportedLanguageEnum: String = ""
}