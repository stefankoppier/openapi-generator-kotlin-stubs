rootProject.name = "openapi-generator-kotlin-stubs"

dependencyResolutionManagement {
    versionCatalogs {
        create("libraries") {
            plugin("kotlin", "org.jetbrains.kotlin.jvm").version("1.7.21")
            plugin("spotless", "com.diffplug.spotless").version("6.12.0")
            plugin("openapi-generator", "org.openapi.generator").version("6.2.1")
            plugin("use-latest-versions", "se.patrikerdes.use-latest-versions").version("0.2.18")
            plugin("versions", "com.github.ben-manes.versions").version("0.44.0")

            library("openapi-generator", "org.openapitools", "openapi-generator").version("6.2.1")
            library("kotlin-test", "org.jetbrains.kotlin", "kotlin-test").version("1.7.22")
            library("junit-params", "org.junit.jupiter", "junit-jupiter-params").version("5.9.1")
            library("mockito-kotlin", "org.mockito.kotlin", "mockito-kotlin").version("4.1.0")
            library("wiremock", "com.github.tomakehurst", "wiremock-jre8").version("2.35.0")
            library("moshi-kotlin", "com.squareup.moshi", "moshi-kotlin").version("1.14.0")
            library("kotlin-builder-dsl", "io.github.stefankoppier", "kotlin-builder-dsl").version("0.0.3")

            library("rest-assured", "io.rest-assured", "kotlin-extensions").version("5.3.0")
        }
    }
}