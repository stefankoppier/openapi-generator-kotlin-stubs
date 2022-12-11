val repository = "openapi-generator-kotlin-stubs"
val organization = "stefankoppier"
val github = "github.com/$organization/$repository"

repositories {
    mavenCentral()
}

plugins {
    id("java")
    id("maven-publish")
    alias(libraries.plugins.kotlin)
    alias(libraries.plugins.spotless)
}

group = "io.github.stefankoppier"
version = "0.0.1"

dependencies {
    implementation(libraries.openapi.generator)
}

publishing {
    publications {
        create<MavenPublication>(repository) {
            from(components["kotlin"])
            artifactId = repository
        }
    }
}

spotless {
    kotlin {
        ktfmt().dropboxStyle().configure { options ->
            options.setMaxWidth(120)
        }
    }
}
