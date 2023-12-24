package dev.shushant.localization

import dev.shushant.localization.utils.Languages


open class LocalizationExtension {
    var supportedLang = listOf<Languages>()
    var flavour: String = "DEV"
    var moduleName: String = ""
    var pathToGenerateSupportedLanguageEnum:String =""
}