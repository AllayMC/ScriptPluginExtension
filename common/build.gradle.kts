dependencies {
    api(libs.polyglot)
    api(libs.chromeinspector)

//    api(group = "org.graalvm.polyglot", name = "js-community", version = graalVersion)
//    api(group = "org.graalvm.polyglot", name = "python-community", version = graalVersion)
//    api(group = "org.graalvm.sdk", name = "graal-sdk", version = graalVersion)
//    api(group = "org.graalvm.truffle", name = "truffle-runtime", version = graalVersion)
//    api(group = "org.graalvm.tools", name = "profiler-tool", version = graalVersion)
}

tasks.jar {
    enabled = false
}