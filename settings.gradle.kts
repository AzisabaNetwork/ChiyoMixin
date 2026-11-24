pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "ChiyoMixin"

include("chiyomixin-api")
include("chiyomixin-server")

// ChiyoMixin - PaperMixin is built as its own (included) build and bundled by :createChiyoMixinJar
includeBuild("chiyomixin-mixin") {
    name = "papermixin"
}
