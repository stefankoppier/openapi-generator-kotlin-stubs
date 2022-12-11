rootProject.name = "openapi-generator-kotlin-stubs"

dependencyResolutionManagement {
    versionCatalogs {
        create("libraries") {
            plugin("kotlin", "org.jetbrains.kotlin.jvm").version("1.7.21")
            plugin("spotless", "com.diffplug.spotless").version("6.12.0")

            library("openapi-generator", "org.openapitools", "openapi-generator").version("6.2.1")
        }
    }
}