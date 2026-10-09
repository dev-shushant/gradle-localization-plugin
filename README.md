# Android Gradle Plugins

A Kotlin Gradle plugin suite for Android localization, resource class generation, and native string obfuscation.

## Engineering focus

The source includes Gradle plugin/task wiring, XML translation generation, resource class generation, and native string/header generation. This repository shows build tooling alongside application development.

## Available plugins

### Native string generation

[Plugin Portal: dev.shushant.ndk-encryption](https://plugins.gradle.org/plugin/dev.shushant.ndk-encryption)

Generates obfuscated native strings and C++ headers at build time. Native string obfuscation raises the effort needed to inspect embedded values; it does not make client-side secrets confidential.

### Localization

[Plugin Portal: dev.shushant.localization.plugin](https://plugins.gradle.org/plugin/dev.shushant.localization.plugin)

Provides translation generation for Android resources and supported-language enum generation. Review `LocalizationPlugin`, translation utilities, and `LocalizationSample` for the implementation and sample configuration.

```kotlin
localization {
    supportedLang = listOf(Languages.Kannada, Languages.Bengali, Languages.Hindi)
    moduleName = ":yourModule"
    pathToGenerateSupportedLanguageEnum = "your/path"
    packageName = "your.package"
}
```

### Resource class generation

[Plugin Portal: dev.shushant.generate-app-resource-class.plugin](https://plugins.gradle.org/plugin/dev.shushant.generate-app-resource-class.plugin)

Generates Kotlin classes for string and drawable resource access.

```kotlin
appResourceExtension {
    stringFilePathWithFileName = "your/path/Strings.kt"
    iconsFilePathWithFileName = "your/path/Icons.kt"
    moduleName = ":yourModule"
    packageNameWhereToGenerateFiles = "your.package"
    applicationId = "your.app.id"
    classNameForIcons = "AppIcons"
    classNameForStrings = "AppStrings"
}
```

## Installation

Select a published version from the relevant Plugin Portal entry and apply its plugin ID in your Gradle plugins block. This documentation update does not establish a release version or validate compatibility with your Gradle/Android plugin versions.

## Feedback

[Report an issue](https://github.com/dev-shushant/gradle-localization-plugin/issues) with the plugin ID, version, Gradle version, and a minimal reproduction.

## License

No license file is present in the current repository tree. Add an explicit license before describing reuse terms.
