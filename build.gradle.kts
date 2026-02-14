plugins {
    java
    application
    id("com.gradleup.shadow") version "9.3.0"
    id("org.danilopianini.gradle-java-qa") version "1.161.0"
}


repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Detecting os
val osName = System.getProperty("os.name").lowercase()
val osArch = System.getProperty("os.arch").lowercase()

// Correct classifier for JavaFX
val javafxPlatform = when {
    osName.contains("win") -> "win"
    osName.contains("mac") -> if (osArch == "aarch64") "mac-aarch64" else "mac"
    osName.contains("nux") || osName.contains("nix") -> if (osArch == "aarch64") "linux-aarch64" else "linux"
    else -> "linux" // Fallback
}

val javaFXModules = listOf("base", "controls", "fxml", "swing", "graphics")
val javaFxVersion = "23.0.2"

dependencies {
    // Suppressions for SpotBugs
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.9.8")

    // YAML parser
    implementation("org.yaml:snakeyaml:2.2")

	// Loads jars for desired platform. no "package not found" errors this way.
    for (module in javaFXModules) {
        implementation("org.openjfx:javafx-$module:$javaFxVersion:$javafxPlatform")
    }

    // Testing
    testImplementation(platform("org.junit:junit-bom:6.0.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val main: String by project

application {
    mainClass.set(main)
}
