plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.koukakios.contextpilot"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

intellij {
    version.set("2023.3")
    type.set("IC")
}

tasks {
    patchPluginXml {
        sinceBuild.set("233")
        untilBuild.set("243.*")
    }
}