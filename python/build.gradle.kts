plugins {
    id("com.gradleup.shadow") version "8.3.2"
}

version = "0.2.0"

dependencies {
    implementation(project(":common"))
    implementation(libs.python)
}

tasks.shadowJar {
    archiveFileName = "PythonPluginExtension-${version}-shaded.jar"
}