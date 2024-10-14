plugins {
    id("java-library")
    id("java-library-distribution")
}

group = "org.allaymc.scriptpluginext"
version = "0.3.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    compileOnly(rootProject.libs.allay)
    compileOnly(rootProject.libs.lombok)

    implementation(libs.polyglot)
    implementation(libs.javascript)
    implementation(libs.python)
    implementation(libs.chromeinspector)

    annotationProcessor(rootProject.libs.lombok)
}

distributions {
    main {
        distributionBaseName.set("ScriptPluginExtension")
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        configureEach {
            options.isFork = true
        }
    }
}
