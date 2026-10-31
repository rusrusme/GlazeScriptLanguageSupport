plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.19.0"
    id("org.jetbrains.intellij.platform.grammarkit") version "2.19.0"
}

val pluginVersion = providers.gradleProperty("pluginVersion")
val pluginGroup = providers.gradleProperty("pluginGroup")

group = pluginGroup.get()
version = pluginVersion.get()

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        clion("2026.2.4")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

sourceSets {
    main {
        java {
            srcDirs("src/main/gen", "src/main/java")
        }
    }
}

tasks {
    generateLexer {
        sourceFile.set(file("src/main/java/net/rusrusme/glaze/grammar/GLexer.flex"))
        targetRootOutputDir.set(file("src/main/gen/net/rusrusme/glaze/lexer"))
        purgeOldFiles.set(true)
    }

    generateParser {
        sourceFile.set(file("src/main/java/net/rusrusme/glaze/grammar/GShader.bnf"))
        targetRootOutputDir.set(file("src/main/gen"))
        pathToParser.set("net/rusrusme/glaze/parser/GParser.java")
        pathToPsiRoot.set("net/rusrusme/glaze/psi")
        purgeOldFiles.set(true)
    }

    compileJava {
        dependsOn(generateLexer, generateParser)
    }
}

intellijPlatform {
    publishing {
        token = providers.gradleProperty("jetbrainsMarketplaceToken")
    }

    pluginConfiguration {
        version = pluginVersion

        description = provider {
            """
            <p></p>
            <h3>Features</h3>
            <ul>
            </ul>
            """.trimIndent()
        }

        changeNotes = pluginVersion.map { ver ->
            """
            <h3>$ver</h3>
            <ul>
                <li>Pre-Release</li>
            </ul>
            """.trimIndent()
        }
    }
}