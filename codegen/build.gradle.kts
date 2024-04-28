import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("application")
}

group = "org.teleight"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("io.soabase.java-composer:java-composer:1.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains:annotations:24.0.1")
}

application {
    mainClass = "org.teleight.teleightbots.codegen.GeneratorMain"
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    archiveVersion.set("")

    archiveFileName.set("codegen.jar")
}
