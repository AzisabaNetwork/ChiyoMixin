import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("io.papermc.paperweight.patcher") version "2.0.0-beta.19"
}

paperweight {
    upstreams.paper {
        ref = providers.gradleProperty("paperRef")

        patchFile {
            path = "paper-server/build.gradle.kts"
            outputFile = file("chiyomixin-server/build.gradle.kts")
            patchFile = file("chiyomixin-server/build.gradle.kts.patch")
        }
        patchFile {
            path = "paper-api/build.gradle.kts"
            outputFile = file("chiyomixin-api/build.gradle.kts")
            patchFile = file("chiyomixin-api/build.gradle.kts.patch")
        }
        patchDir("paperApi") {
            upstreamPath = "paper-api"
            excludes = setOf("build.gradle.kts")
            patchesDir = file("chiyomixin-api/paper-patches")
            outputDir = file("paper-api")
        }
    }
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
        options.isFork = true
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }
    tasks.withType<Test> {
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }

    extensions.configure<PublishingExtension> {
        repositories {
            /*
            maven("https://repo.papermc.io/repository/maven-snapshots/") {
                name = "paperSnapshots"
                credentials(PasswordCredentials::class)
            }
             */
        }
    }
}

// ChiyoMixin start - bundle PaperMixin into the paperclip jar
//
// The distributed jar is the normal Paper-style paperclip jar (it downloads the vanilla server from Mojang
// and patches it on first start), with PaperMixin added on top. PaperMixin is started through the
// "Launcher-Agent-Class" manifest attribute, so `java -jar ChiyoMixin-paperclip.jar` is enough;
// no -javaagent flag is needed. Mixin modules go into <server root>/modules.
val paperMixinBuild = gradle.includedBuild("papermixin")

val createChiyoMixinJar by tasks.registering(Jar::class) {
    group = "chiyomixin"
    description = "Creates the ChiyoMixin paperclip jar (mojmap) with PaperMixin bundled"

    val mcVersion = providers.gradleProperty("mcVersion").get()
    val projectVersion = project.version.toString()
    dependsOn(":chiyomixin-server:createMojmapPaperclipJar")
    dependsOn(paperMixinBuild.task(":shadowJar"))

    val paperclipJar = layout.projectDirectory.file("chiyomixin-server/build/libs/chiyomixin-paperclip-$projectVersion-mojmap.jar")
    val paperMixinJar = layout.projectDirectory.file("chiyomixin-mixin/build/libs/PaperMixin-1.0.0-SNAPSHOT-all.jar")

    from(zipTree(paperclipJar)) {
        exclude("META-INF/MANIFEST.MF")
    }
    from(zipTree(paperMixinJar)) {
        // plugin.yml is only used when PaperMixin is installed as a plugin
        exclude("META-INF/MANIFEST.MF", "plugin.yml")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    archiveFileName = "ChiyoMixin-paperclip-$projectVersion.jar"
    destinationDirectory = layout.buildDirectory.dir("libs")

    manifest {
        attributes(
            "Main-Class" to "io.papermc.paperclip.Main",
            "Launcher-Agent-Class" to "net.azisaba.papermixin.PaperMixinAgent",
            "Premain-Class" to "net.azisaba.papermixin.PaperMixinAgent",
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true",
            "Enable-Native-Access" to "ALL-UNNAMED",
            "Implementation-Title" to "ChiyoMixin",
            "Implementation-Version" to projectVersion,
            "Implementation-Vendor" to "AzisabaNetwork",
            "Minecraft-Version" to mcVersion,
        )
    }
}
// ChiyoMixin end
