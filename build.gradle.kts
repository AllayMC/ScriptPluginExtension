plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.allaymc.scriptpluginext"
description = "Script plugin extension for allay server"
version = "0.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://www.jitpack.io/")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    compileOnly(group = "org.allaymc.allay", name = "server", version = "master-SNAPSHOT")
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.34")

    implementation(group = "org.graalvm.polyglot", name = "js-community", version = "24.1.0")
    implementation(group = "org.graalvm.polyglot", name = "polyglot", version = "24.1.0")
    implementation(group = "org.graalvm.sdk", name = "graal-sdk", version = "24.1.0")
    implementation(group = "org.graalvm.tools", name = "chromeinspector-tool", version = "24.1.0")
    implementation(group = "org.graalvm.tools", name = "profiler-tool", version = "24.1.0")

    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.34")
}

tasks.shadowJar {
    archiveClassifier = "shaded"
}