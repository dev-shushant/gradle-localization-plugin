@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("jvm") version "1.9.10"
    id("com.gradle.plugin-publish") version "1.2.1"
    `java-gradle-plugin`
    `kotlin-dsl`
}

group = "dev.shushant"
version = "1.0.2-alpha"

repositories {
    mavenCentral()
}

gradlePlugin {
    website = "https://www.shushant.dev/"
    vcsUrl = "https://github.com/dev-shushant/gradle-localization-plugin.git"
    plugins {
        create("localizationPlugin") {
            id = "dev.shushant.localization.plugin"
            implementationClass = "dev.shushant.localization.plugin.LocalizationPlugin"
            displayName = "App localization plugin"
            description =
                "Simplify and streamline the localization process for your Android app with the App Localization Plugin.\n"
            tags = listOf("localization", "androidApp", "composeSupport")
        }
        create("encryptionPlugin") {
            id = "dev.shushant.ndk-encryption"
            implementationClass = "dev.shushant.localization.plugin.EncryptionPlugin"
            displayName = "NDK Encryption Plugin"
            description = """
        Enhance your Android app's security with the NDK Encryption Plugin.
        This plugin provides a simple and efficient way to integrate native encryption features into your project.
    """.trimIndent()
            tags = listOf("encryption", "androidApp", "ndk", "security")
        }
        create("generateAppResourceClass") {
            id = "dev.shushant.generate-app-resource-class.plugin"
            implementationClass = "dev.shushant.localization.plugin.GenerateAppResourceClassPlugin"
            displayName = "Generate app resource classes"
            description =
                "Simplify resource management in your Jetpack Compose project with the Generate App Resource Class plugin. This Gradle plugin automates the creation of a Kotlin class that acts as a central hub for accessing application resources such as strings and drawables directly from your Composables."
            tags = listOf(
                "android",
                "jetpack",
                "compose",
                "resource-management",
                "string-resources",
                "drawable-resources",
                "gradle-plugin"
            )
        }
    }
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(gradleTestKit())
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}

