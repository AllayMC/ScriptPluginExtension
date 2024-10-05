plugins {
    id("java-library")
}

tasks.jar {
    enabled = false
}

subprojects {
    apply(plugin = "java-library")

    group = "org.allaymc.scriptpluginext"

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
        compileOnly(rootProject.libs.allay)
        compileOnly(rootProject.libs.lombok)

        annotationProcessor(rootProject.libs.lombok)
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            configureEach {
                options.isFork = true
            }
        }
    }
}