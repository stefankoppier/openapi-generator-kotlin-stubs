# OpenAPI generator kotlin-stubs
Generate Kotlin [WireMock](https://wiremock.org/) stubs from an OpenAPI specification.

## Usage with Gradle
First, add the dependency to the classpath of which OpenAPI generator executes
```kotlin
buildscript {
    repositories {
        mavenCentral()
    }
    
    dependencies {
        classpath("io.github.stefankoppier:openapi-generator-kotlin-stubs:0.0.1")
    }
    
    plugins {
        id("org.openapi.generator") version "6.2.1"
    }
}
```
then configure `openApiGenerate`
```kotlin
openApiGenerate {
    generatorName.set("kotlin-stubs")
    inputSpec.set("path-to-your-api")
    apiPackage.set("me.someone.api.stubs")
    modelPackage.set("me.someone.api.stubs.models")
}
```
and finally run the generator via executing the Gradle task `openApiGenerate`.

### Dependencies
The following dependencies are necessary to build the generated stubs:
```kotlin
dependencies {
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")

    implementation("io.github.stefankoppier:kotlin-builder-dsl:0.0.2")
    implementation("com.github.tomakehurst:wiremock-jre8:2.35.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
}
```