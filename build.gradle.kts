import org.jetbrains.intellij.tasks.PatchPluginXmlTask

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.intellij") version "1.16.0"
}

group = "com.koukakios.intellijassist"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

intellij {
    version.set("2023.3")
    type.set("IC")
    plugins.set(listOf("java"))
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("233")
        untilBuild.set("240.*")
    }
}
