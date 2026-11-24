pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// ChiyoMixin - PaperMixin (https://github.com/AzisabaNetwork/PaperMixin), bundled into the ChiyoMixin jar
rootProject.name = "PaperMixin"
include("helper")
include("builtin")
include("bridge")
