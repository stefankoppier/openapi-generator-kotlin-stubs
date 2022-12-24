package io.github.stefankoppier.generators

import com.google.common.collect.ImmutableMap
import com.samskivert.mustache.Mustache
import java.io.File
import org.openapitools.codegen.*
import org.openapitools.codegen.languages.AbstractKotlinCodegen
import org.openapitools.codegen.model.ModelMap
import org.openapitools.codegen.model.ModelsMap
import org.openapitools.codegen.model.OperationsMap

class KotlinStubsGenerator : AbstractKotlinCodegen(), CodegenConfig {

    private var apiVersion = "1.0.0"

    override fun getTag() = CodegenType.OTHER

    override fun getName() = "kotlin-stubs"

    override fun getHelp() = "Generates Wiremock stubs written in Kotlin."

    init {
        outputFolder = "generated-code/generator"
        modelTemplateFiles["model.mustache"] = ".kt"
        apiTemplateFiles["api.mustache"] = ".kt"
        templateDir = "kotlin-stubs"
        apiPackage = "org.openapitools.api"
        modelPackage = "org.openapitools.model"

        enumPropertyNaming = CodegenConstants.ENUM_PROPERTY_NAMING_TYPE.UPPERCASE

        additionalProperties["apiVersion"] = apiVersion

        typeMapping["array"] = "kotlin.collections.List"
    }

    override fun processOpts() {
        super.processOpts()

        val folder = "$sourceFolder.$apiPackage.internal".replace(".", File.separator)
        supportingFiles.addAll(
            arrayOf(
                SupportingFile("internal/abstract-stub-builder.mustache", folder, "AbstractStubBuilder.kt"),
                SupportingFile("internal/abstract-stub.mustache", folder, "AbstractStub.kt"),
                SupportingFile("internal/moshi-adapters.mustache", folder, "MoshiAdapters.kt"),
            ))
    }

    override fun setEnumPropertyNaming(enumPropertyNamingType: String?) {
        super.setEnumPropertyNaming(enumPropertyNamingType)
    }

    override fun postProcessAllModels(objects: MutableMap<String, ModelsMap>): MutableMap<String, ModelsMap> {
        super.postProcessAllModels(objects).forEach { obj ->
            obj.value.models.forEach { model -> model.model.vars.forEach(CodegenProperty::normalize) }
        }
        return super.postProcessAllModels(objects)
    }

    override fun postProcessOperationsWithModels(objects: OperationsMap, allModels: List<ModelMap>): OperationsMap {
        objects.operations.operation.forEach { operation ->
            operation.responses.forEach<CodegenResponse>(CodegenResponse::normalize)
            operation.normalize()
        }
        return objects
    }

    override fun addMustacheLambdas(): ImmutableMap.Builder<String, Mustache.Lambda> {
        return super.addMustacheLambdas().put("readableHttpCode", ReadableCodeLambda())
    }

    override fun postProcess() {
        println("#####################################################################################")
        println("# Thanks for using OpenAPI Generator.                                               #")
        println("# Please consider donation to help us maintain this project                         #")
        println("# https://opencollective.com/openapi_generator/donate                               #")
        println("#                                                                                   #")
        println("# This generator's contributed by Stefan Koppier (https://github.com/StefanKoppier) #")
        println("#####################################################################################")
    }
}
