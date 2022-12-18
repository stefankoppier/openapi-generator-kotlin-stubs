# OpenAPI generator kotlin-stubs
Generate Kotlin [WireMock](https://wiremock.org/) stubs from an OpenAPI specification. 

### Functionality
The generator will generate:
- Stubbing interfaces for each operation.
- Stub builders for each possible response of each operation, which are injected in WireMock.
- Models for each schema.
- Builders which generate can generate these models with i.e. random data within the constraints of the API.

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

## Example
We have a simple API which manages users.

## Paths and Operations
Suppose we have two paths `/users` and `users/{userId}`
```yaml
openapi: 3.0.3
info:
  title: Demo API
  version: 0.0.1

paths:
  /users:
    $ref: '#/components.operations.Users'
  /user/{userId}:
    $ref: '#/components.operations.User'
```
which contain the following operation for `/users`
```yaml
components.operations.Users:
  get:
    responses:
      '200':
        content:
          application/json:
            schema:
              $ref: '#/components.schemas.Ok'
```
and the following operation for `/user/{userId}`
```yaml
components.operations.User:
  get:
    parameters:
      - in: path
        name: userId
        description: The id of the user to retrieve.
        schema:
          type: integer
        required: true
      - in: query
        name: withAge
        description: Includes the age of the user in the result if set to true.
        schema:
          type: boolean
    responses:
      '200':
        content:
          application/json:
            schema:
              $ref: '#/components.schemas.User'
      '404':
        description: User was not found.
  post:
    parameters:
      - in: path
        name: userId
        schema:
          type: integer
        required: true
    responses:
      '200':
        content:
          application/json:
            schema:
              $ref: '#/components.schemas.User'
```

This will generate a class `DefaultApiStub` with a stub for each operation. 
For example, the `/users` GET operation will generate 
```kotlin
fun userUserIdGet(userId: kotlin.Int, withAge: StringValuePattern = AnythingPattern()): UserUserIdGetStubBuilder {
    val path = parameterized("/user/{userId}", userId)
    return UserUserIdGetStubBuilder(WireMock.get(path)
        .withQueryParam("withAge", withAge)
    )
}

class UserUserIdGetStubBuilder internal constructor(stub: MappingBuilder) : AbstractStubBuilder(stub) {
    
    fun ok(body: User) {
        val adapter = moshi.adapter(User::class.java)
        stubFor(stub.willReturn(WireMock.status(200).withBody(adapter.toJson(body))))
    }

    fun notFound() {
        stubFor(stub.willReturn(WireMock.status(404)))
    }
}
```
which can be used as e.g. `stub.userUerIdGet(-1).notFound()`.

## Responses
The response of the `/users` is defined as 
```yaml
components.responses.Ok:
  type: array
  items:
    $ref: '#/components.schemas.User'
```

## Schemas
We define a user as
```yaml
components.schemas.User:
  type: object
  required:
    - firstname
    - lastname
    - address
    - age
  properties:
    firstname:
      type: string
      example: Arthur
      minLength: 1
    lastname:
      type: string
      example: Dent
      minLength: 1
      maxLength: 30
    gender:
      $ref: '#/components.schemas.Gender'
    age:
      type: integer
      minimum: 0
      example: 34
    address:
      $ref: '#/components.schemas.Address'
```
which will generate two classes: 1. a data class `User`; and 2. a builder `UserBuilder`.

The first class that is generated, the data class `User`, will have the following structure
```kotlin
data class User(
    @Json(name = "firstname") var firstname: String,
    @Json(name = "lastname") var lastname: String,
    @Json(name = "age") var age: Int,
    @Json(name = "address") var address: Address,
    @Json(name = "gender") var gender: Gender?) {

    companion object {
        fun of(transform: UserBuilder.() -> UserBuilder = { UserBuilder() }): User {
            return transform(UserBuilder())()
        }
    }
}
```
consisting of a mutable field for each property in the schema. And an
`of` function. This method can be used to invoke the builder in a more user-friendly manner.
For example
```kotlin
User.of {
    firstname { constant { "Jane" } }
}
```
will generate a `User` with random values within the constraints of
the API and `firstname` set to `"Jane"`.

This builder is based on the package 
[io.github.stefankoppier:kotlin-builder-dsl](https://github.com/StefanKoppier/kotlin-builder-dsl)
and is structured as
```kotlin
class UserBuilder : BuilderDsl<User> {

    private var firstname = StringBuilder().min(1)
    private var lastname = StringBuilder().min(1).max(30)
    private var age = IntBuilder().min(0)
    private var address = AddressBuilder()
    private var gender = GenderBuilder()

    override operator fun invoke(): User {
        return User(
            firstname = firstname.invoke(),
            lastname = lastname.invoke(),
            age = age.invoke(),
            address = address.invoke(),
            gender = gender.invoke()
        )
    }

    fun firstname(transform: StringBuilder.() -> StringBuilder = { StringBuilder() }): UserBuilder {
        firstname = transform(StringBuilder())
        return this
    }

    fun lastname(transform: StringBuilder.() -> StringBuilder = { StringBuilder() }): UserBuilder {
        lastname = transform(StringBuilder())
        return this
    }

    fun age(transform: IntBuilder.() -> IntBuilder = { IntBuilder() }): UserBuilder {
        age = transform(IntBuilder())
        return this
    }

    fun address(transform: AddressBuilder.() -> AddressBuilder = { AddressBuilder() }): UserBuilder {
        address = transform(AddressBuilder())
        return this
    }

    fun gender(transform: GenderBuilder.() -> GenderBuilder = { GenderBuilder() }): UserBuilder {
        gender = transform(GenderBuilder())
        return this
    }
}
```
where the default value for each property is a random value within the 
constraints of the API.

#### Enums
There is support for enums. We define the gender of an user as
```yaml
components.schemas.Gender:
  type: string
  enum:
    - MALE
    - FEMALE
```
which will also generate a data class and a builder
```kotlin
enum class Gender(val value: String) {
    @Json(name = "MALE") MALE("MALE"),
    @Json(name = "FEMALE") FEMALE("FEMALE");

    companion object {
        fun of(transform: GenderBuilder.() -> GenderBuilder = { GenderBuilder() }): Gender {
            return transform(GenderBuilder())()
        }
    }

    override fun toString(): String {
        return value
    }
}

class GenderBuilder: EnumBuilder<Gender>() {

    override fun allValues(): Array<Gender> {
        return Gender.values()
    }
}
```

#### Nested Types
Each `User` has an address which is defined as
```yaml
components.schemas.Address:
  type: object
  properties:
    street:
      type: string
      example: Amsterdamsestraatweg 427
    zip:
      type: string
      example: 3553 EA
    city:
      type: string
      example: Utrecht
```
which will generate the following code, similar to the other schemas
```kotlin
data class Address(
    @Json(name = "street") var street: String?,
    @Json(name = "zip") var zip: String?,
    @Json(name = "city") var city: String?) { 

    companion object {
        fun of(transform: AddressBuilder.() -> AddressBuilder = { AddressBuilder() }): Address {
            return transform(AddressBuilder())()
        }
    }
}

class AddressBuilder : BuilderDsl<Address> {

    private var street = StringBuilder()
    private var zip = StringBuilder()
    private var city = StringBuilder()

    override operator fun invoke(): Address {
        return Address(
            street = street.invoke(),
            zip = zip.invoke(),
            city = city.invoke()
        )
    }

    fun street(transform: StringBuilder.() -> StringBuilder = { StringBuilder() }): AddressBuilder {
        street = transform(StringBuilder())
        return this
    }

    fun zip(transform: StringBuilder.() -> StringBuilder = { StringBuilder() }): AddressBuilder {
        zip = transform(StringBuilder())
        return this
    }

    fun city(transform: StringBuilder.() -> StringBuilder = { StringBuilder() }): AddressBuilder {
        city = transform(StringBuilder())
        return this
    }
}
```
of which the builder of the `Address` can be used from within the builder
of the `User`
```kotlin
User.of {
    address { city { constant("Utrecht") } }
}
```