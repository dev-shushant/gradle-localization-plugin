# 🚀 Shushant's Gradle Plugins

[![GitHub](https://img.shields.io/badge/GitHub-Plugin%20Repo-blue)](https://github.com/dev-shushant/gradle-localization-plugin)

A suite of purpose-built Gradle plugins to enhance Android development — from native encryption to localization and streamlined resource access.

---

## 📦 Available Plugins

- [NDK Encryption Plugin](#-ndk-encryption-plugin)
- [Localization Plugin](#-localization-plugin)
- [Generate App Resource Class Plugin](#-generate-app-resource-class-plugin)

---

<details>
<summary>🔐 <strong>NDK Encryption Plugin</strong></summary>

[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v?color=blue&label=gradle&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fdev%2Fshushant%2Fndk-encryption%2Fdev.shushant.ndk-encryption.gradle.plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/dev.shushant.ndk-encryption)

Securely manage secrets in native Android code (C++/JNI) — all at build time. Fully configurable, flavor-aware, and CI/CD-friendly.

### ✅ Features

- Build-time native secret embedding (C++/JNI)
- Per-flavor secret configuration
- Remote secret fetching support
- Obfuscated C++ logic generation
- CI/CD integration ready

### 🚀 Getting Started

```kotlin
plugins {
    id("dev.shushant.ndk-encryption") version "x.x.x"
}
```

</details>

---

<details>
<summary>🌍 <strong>Localization Plugin</strong></summary>

[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v?color=blue&label=gradle&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fdev%2Fshushant%2Flocalization%2Fplugin%2Fdev.shushant.localization.plugin.gradle.plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/dev.shushant.localization.plugin)

Automate localization for Android apps with resource extraction, translation service integration, and enum generation for supported languages.

### ✅ Features

- Extract translatable strings from XML/code
- Generate localized resource files
- Integrate with translation services
- Generate enums for supported languages

### 🚀 Getting Started

```kotlin
plugins {
    id("dev.shushant.localization.plugin") version "x.x.x"
}

localization {
    supportedLang = listOf(
        Languages.Kannada,
        Languages.Bengali,
        Languages.Hindi,
        // ...
    )
    moduleName = ":yourModule"
    pathToGenerateSupportedLanguageEnum = "your/path"
    packageName = "your.package"
}
```

</details>

---

<details>
<summary>🧩 <strong>Generate App Resource Class Plugin</strong></summary>

[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v?color=blue&label=gradle&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fdev%2Fshushant%2Fgenerate-app-resource-class%2Fplugin%2Fdev.shushant.generate-app-resource-class.plugin.gradle.plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/dev.shushant.generate-app-resource-class.plugin)

Generate Kotlin classes for accessing string and drawable resources in Jetpack Compose — with clean, type-safe APIs.

### ✅ Features

- Kotlin class generation for string and drawable access
- Extension functions for drawables
- Centralized, type-safe resource management
- Compose-optimized integration

### 🚀 Getting Started

```kotlin
plugins {
    id("dev.shushant.generate-app-resource-class.plugin") version "x.x.x"
}

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

### 📘 Example Usage

```kotlin
val welcomeMessage: String = AppStrings.welcomeMessage
val appIcon: Painter = AppIcons.appIcon
```

</details>

---

## 🤝 Contributions & Feedback

Found a bug? Have a suggestion?  
Open an issue or submit a pull request via [GitHub](https://github.com/dev-shushant/gradle-localization-plugin).

We welcome feedback, ideas, and contributions from the community.

---

## 📄 License

This project is licensed under the MIT License.
