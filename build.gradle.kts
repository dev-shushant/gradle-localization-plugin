@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("jvm") version "1.9.10"
    id("com.gradle.plugin-publish") version "1.2.1"
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

group = "dev.shushant"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
}

gradlePlugin {
    website = "https://github.com/ysb33r/gradleTest"
    vcsUrl = "https://github.com/ysb33r/gradleTest.git"
    plugins {
        create("localizationPlugin") {
            id = "dev.shushant.localization.plugin"
            implementationClass = "dev.shushant.localization.LocalizationPlugin"
            displayName = "App localization plugin"
            description = """
        Simplify and streamline the localization process for your Android app with the App Localization Plugin.
        
        This Gradle plugin automates key aspects of localization, offering a seamless integration into your build process. Say goodbye to tedious manual translation management and ensure a consistent user experience across different languages.

        Features:
        - Automated extraction of translatable strings from your source code and XML resources.
        - Integration with popular translation services for efficient and accurate translations.
        - Streamlined build process to generate localized resource files automatically.
        - Improved collaboration between developers and translators with clear, standardized workflows.

        Whether you're a solo developer or part of a team, the App Localization Plugin empowers you to internationalize your app effortlessly. Focus on building great features while leaving the localization complexities to this versatile Gradle plugin.

        Get started with just a few lines of configuration and unlock the full potential of globalizing your Android app!
    """
        }
    }
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

