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
        sourceFile.set(file("src/main/java/net/rusrusme/glaze/grammar/GlazeScript.bnf"))
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
            <p>GlazeScript (<code>.gscript</code>) language support for writing gameplay systems and components in GlacialEngine.</p>
            
            <h3>What's included</h3>
            <ul>
                <li>Syntax highlighting for primitive types, GLM math types (<code>vec2</code>–<code>vec4</code>, <code>mat2</code>–<code>mat4</code>, <code>quat</code>), <code>list&lt;T&gt;</code>, and engine types (<code>entity</code>, <code>event</code>).</li>
                <li>Code completion for entity and list methods (<code>get</code>, <code>add</code>, <code>has</code>, <code>remove</code>, <code>clear</code>), vector swizzles (<code>.xyzw</code>), and custom struct/component fields.</li>
                <li>Built-in GLM math utilities (<code>normalize</code>, <code>cross</code>, <code>dot</code>, <code>distance</code>, <code>clamp</code>, <code>mix</code>, <code>slerp</code>, etc.) with argument and type validation.</li>
                <li>Live templates for loop scaffolding (<code>for</code> indexed, range, and ECS queries), system definitions, and update/event hooks.</li>
                <li>Editor inspections that catch missing update/event hooks in systems and flag ECS queries placed outside system blocks.</li>
                <li>Auto-closing quotes (<code>""</code>, <code>''</code>), paired bracket matching, and 4-space code formatting.</li>
            </ul>
            """.trimIndent()
        }

        changeNotes = pluginVersion.map { ver ->
            """
            <h3>$ver</h3>
            <ul>
                <li>Initial release of GlazeScript support for CLion and IntelliJ.</li>
                <li>Lexer and parser implementation covering the full language grammar.</li>
                <li>Autocomplete for custom component/struct fields, list/entity intrinsics, and vector swizzles.</li>
                <li>Support for built-in GLM math utilities with argument count and type checking.</li>
                <li>Live templates for indexed, range, and ECS view query loops, systems, and callbacks.</li>
                <li>Auto-closing quotes for strings and char literals, plus template support for new <code>.gscript</code> files.</li>
                <li>Code style formatter (4 spaces) and configurable syntax color page.</li>
            </ul>
            """.trimIndent()
        }
    }
}