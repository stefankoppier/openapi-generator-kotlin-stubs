val repository = "openapi-generator-kotlin-stubs"
val organization = "stefankoppier"
val github = "github.com/$organization/$repository"

group = "io.github.stefankoppier"
version = "0.0.1"

buildscript {
    dependencies {
        classpath(fileTree("$buildDir/libs/"))
    }

    plugins {
        alias(libraries.plugins.openapi.generator)
    }
}
repositories {
    mavenCentral()
}

plugins {
    id("java")
    id("java-test-fixtures")
    id("maven-publish")
    alias(libraries.plugins.kotlin)
    alias(libraries.plugins.spotless)
    alias(libraries.plugins.use.latest.versions)
    alias(libraries.plugins.versions)
}

dependencies {
    implementation(libraries.openapi.generator)

    testFixturesImplementation(libraries.moshi.kotlin)

    testImplementation(libraries.kotlin.test)
    testImplementation(libraries.junit.params)
    testImplementation(libraries.mockito.kotlin)
    testImplementation(libraries.wiremock)
    testImplementation(libraries.moshi.kotlin)
    testImplementation(libraries.kotlin.builder.dsl)
    testImplementation(libraries.rest.assured)
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
                        url.set("https://github.com/stefankoppier")
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

tasks.test {
    useJUnitPlatform()
}

spotless {
    kotlin {
        ktfmt().dropboxStyle().configure { options ->
            targetExclude("$buildDir/generated/src/main/kotlin/**/*.*")
            options.setMaxWidth(120)
        }
    }
}

openApiGenerate {
    generatorName.set("kotlin-stubs")
    inputSpec.set("$rootDir/src/test/resources/api/api.yml")
    apiPackage.set("io.github.stefankoppier.generators.test")
    outputDir.set("$buildDir/generated")
    modelPackage.set("io.github.stefankoppier.generators.test.models")
}

sourceSets {
    test {
        kotlin.srcDir("$buildDir/generated/src/main/kotlin")
    }
}

tasks.findByName("openApiGenerate")?.finalizedBy(tasks.findByName("spotlessApply"))