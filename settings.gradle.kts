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
// paperweight runs this settings file again as a nested build while applying patches,
// and Gradle does not allow includeBuild there, so only include it in the top-level build.
if (gradle.parent == null) {
    includeBuild("chiyomixin-mixin") {
        name = "papermixin"
    }
}
