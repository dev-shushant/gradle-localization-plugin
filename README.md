# Gradle Localization Plugin

[![GitHub](https://img.shields.io/badge/GitHub-gradle--localization--plugin-blue)](https://github.com/dev-shushant/gradle-localization-plugin)

https://plugins.gradle.org/plugin/dev.shushant.localization.plugin
https://plugins.gradle.org/plugin/dev.shushant.generate-app-resource-class.plugin

## Overview

The Gradle Localization Plugin is a powerful tool designed to simplify and streamline the localization process for Android applications. Whether you're a solo developer or part of a team, this Gradle plugin automates key aspects of localization, offering a seamless integration into your build process. Say goodbye to tedious manual translation management and ensure a consistent user experience across different languages.

## Features

- **Automated Extraction:** Extract translatable strings from your source code and XML resources effortlessly.
- **Translation Service Integration:** Seamlessly integrate with popular translation services for efficient and accurate translations.
- **Automatic Resource File Generation:** Streamline your build process to automatically generate localized resource files.
- **Collaboration Enhancement:** Improve collaboration between developers and translators with clear, standardized workflows.

## Getting Started

1. Apply the plugin in your project's app-level `build.gradle` file.

```gradle
plugins {
    id 'dev.shushant.localization.plugin' version 'x.x.x'
}

//Use this extension to configure the plugin:

localization {
   // this Languages class is available to choose the supported language.
        supportedLang = listOf(
            Languages.Kannada,
            Languages.Bengali,
            Languages.Hindi,
            etc etc...
        )
        moduleName = ":moduleName",
        // these param will generate the list of supported language enum to your codebase
        pathToGenerateSupportedLanguageEnum = "Language Enum path",
        packageName = ""
    }
```

2. Configure the plugin with your desired settings.

3. Build your project to automatically generate the localized resource files.

4. Unlock the full potential of globalizing your Android app!


# Generate App Resource Class Plugin

[![GitHub](https://img.shields.io/badge/GitHub-generateAppResourceClass--plugin-blue)](https://github.com/dev-shushant/gradle-localization-plugin)

## Overview

The Generate App Resource Class Plugin simplifies resource management in Jetpack Compose projects. This Gradle plugin automates the creation of a Kotlin class that serves as a central hub for accessing application resources directly from your Composables.

## Features

- **Automatic Generation:** Generate a Kotlin class with constants for all string resources, ensuring easy and type-safe access.
- **Drawable Resource Access:** Access drawable resources through generated extension functions.
- **Seamless Integration:** Integrate seamlessly into your build process to keep the resource class up-to-date.
- **Code Readability and Maintainability:** Improve code readability and maintainability by centralizing resource access.

## Getting Started

1. Apply the plugin in your project's app-level `build.gradle` file.
```gradle
plugins {
    id 'dev.shushant.generate-app-resource-class.plugin' version 'x.x.x'
}

//Use this extension to configure the plugin:

appResourceExtension {
       // provide the path where you want to generate the string resource class file
        stringFilePathWithFileName = "",
      // provide the path where you want to generate the drawable resource class file
        iconsFilePathWithFileName = "",
      // moduleName
       moduleName = "",
      // packageNameWhereToGenerateFiles
      packageNameWhereToGenerateFiles = "",
      // to import R file to access resources
      applicationId ="",
      classNameForIcons ="AppIcons",
      classNameForStrings ="AppStrings",
    }
```

2. Build your project to automatically generate the AppResource class.

3. Access strings and drawables directly from your Composables using the generated class.

4. Enhance your Compose development workflow by leveraging the Generate App Resource Class plugin.

## Example Usage

```kotlin
// Access string resource as string to use in composables
val welcomeMessage: String = FileName.welcomeMessage

// Access drawable resource as Painter to use in composables 
val appIcon: Painter = FileName.appIcon
```

Feel free to explore the respective GitHub repositories for each plugin for more details and updates. If you encounter any issues or have suggestions, please open an issue on the GitHub repository. We appreciate your contributions and feedback!
