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

            pom {
                name.set("$group:$repository")
                description.set("Generate WireMock stubs in Kotlin from an OpenAPI specification")
                url.set("https://$github")


                developers {
                    developer {
                        id.set(organization)
                        name.set("Stefan Koppier")
                        email.set("stefan.koppier@outlook.com")
                        url.set("https://github.com/StefanKoppier")
                    }
                }

                scm {
                    connection.set("scm:git:git@$github.git")
                    developerConnection.set("scm:git:git@$github.git")
                    url.set("https://$github/tree/main")
                }

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://$github/issues")
                }
            }
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
