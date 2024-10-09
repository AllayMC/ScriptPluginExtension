plugins {
    id("java-library-distribution")
}

version = "0.2.0"

dependencies {
    implementation(project(":common"))
    implementation(libs.javascript)
}

distributions {
    main {
        distributionBaseName.set("JavaScriptPluginExtension")
    }
}