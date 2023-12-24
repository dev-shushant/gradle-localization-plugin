package dev.shushant.localization.plugin

import dev.shushant.localization.plugin.utils.Languages


open class LocalizationExtension {
    var supportedLang = listOf<Languages>()
    var flavour: String = "DEV"
    var moduleName: String = ""
    var pathToGenerateSupportedLanguageEnum:String =""
}