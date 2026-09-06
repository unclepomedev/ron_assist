import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.GenerateLexerTask
import org.jetbrains.intellij.platform.gradle.tasks.GenerateParserTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
    id("org.jetbrains.intellij.platform.grammarkit")
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    intellijPlatform {
        intellijIdea("2026.1")
        bundledPlugin("org.intellij.plugins.markdown")
        bundledModule("intellij.spellchecker")
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    instrumentCode = false

    pluginConfiguration {
        name = "RON Assist"
        version = providers.gradleProperty("version")
        description =
            providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                with(it.lines()) {
                    if (!containsAll(listOf(start, end))) {
                        throw GradleException(
                            "Plugin description section not found in README.md:\n$start ... $end"
                        )
                    }
                    subList(indexOf(start) + 1, indexOf(end))
                        .joinToString("\n")
                        .let(::markdownToHTML)
                }
            }

        val changelog = project.changelog
        changeNotes =
            providers.gradleProperty("version").map { pluginVersion ->
                with(changelog) {
                    renderItem(
                        (getOrNull(pluginVersion) ?: getUnreleased())
                            .withHeader(false)
                            .withEmptySections(false),
                        Changelog.OutputType.HTML,
                    )
                }
            }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        channels = providers.environmentVariable("RELEASE_CHANNEL").map { listOf(it) }
    }
}

sourceSets {
    main {
        java.srcDirs("src/main/gen")
    }
}

tasks {
    named<GenerateLexerTask>("generateLexer") {
        sourceFile.set(file("src/main/grammar/Ron.flex"))
        targetRootOutputDir.set(file("src/main/gen"))
        pathToClass.set("com/github/unclepomedev/ronassist/lexer/RonLexer.java")
        purgeOldFiles.set(true)
    }

    named<GenerateParserTask>("generateParser") {
        sourceFile.set(file("src/main/grammar/Ron.bnf"))
        targetRootOutputDir.set(file("src/main/gen"))
        pathToParser.set("/com/github/unclepomedev/ronassist/parser/RonParser.java")
        pathToPsiRoot.set("/com/github/unclepomedev/ronassist/psi")
        purgeOldFiles.set(true)
    }

    withType<KotlinCompile> {
        dependsOn("generateLexer", "generateParser")
    }
}
