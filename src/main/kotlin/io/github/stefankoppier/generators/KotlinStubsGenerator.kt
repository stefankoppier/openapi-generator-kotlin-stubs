package io.github.stefankoppier.generators

import com.google.common.collect.ImmutableMap
import com.samskivert.mustache.Mustache
import io.swagger.v3.oas.models.Operation
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

    override fun getHelp(): String {
        return "Generates Wiremock stubs written in Kotlin."
    }

    init {
        outputFolder = "generated-code/generator"
        modelTemplateFiles["model.mustache"] = ".kt"
        apiTemplateFiles["api.mustache"] = ".kt"
        templateDir = "kotlin-stubs"
        apiPackage = "org.openapitools.api"
        modelPackage = "org.openapitools.model"

        additionalProperties["apiVersion"] = apiVersion

        typeMapping["array"] = "kotlin.collections.List"
        typeMapping["string"] = "String"
        typeMapping["boolean"] = "Boolean"
        typeMapping["integer"] = "Int"
        typeMapping["float"] = "Float"
        typeMapping["long"] = "Long"
        typeMapping["double"] = "Double"
        typeMapping["ByteArray"] = "ByteArray"

        languageSpecificPrimitives.addAll(
            listOf(
                "String",
                "Boolean",
                "Int",
                "Float",
                "Long",
                "Double",
                "ByteArray",
            ))
    }

    override fun processOpts() {
        super.processOpts()

        val folder = (sourceFolder + File.separator + apiPackage + ".internal").replace(".", File.separator)
        supportingFiles.addAll(
            arrayOf(
                SupportingFile("internal-abstract-stub-builder.mustache", folder, "AbstractStubBuilder.kt"),
                SupportingFile("internal-abstract-stub.mustache", folder, "AbstractStub.kt"),
            ))
    }

    override fun addOperationToGroup(
        tag: String?,
        resourcePath: String?,
        operation: Operation?,
        co: CodegenOperation?,
        operations: MutableMap<String, MutableList<CodegenOperation>>?
    ) {
        var basePath = resourcePath!!
        if (basePath.startsWith("/")) {
            basePath = basePath.substring(1)
        }
        val pos = basePath.indexOf("/")
        if (pos > 0) {
            basePath = basePath.substring(0, pos)
        }

        if (basePath.isEmpty()) {
            basePath = "default"
        } else {
            co!!.subresourceOperation = co.path.isNotEmpty()
        }
        val opList = operations!!.computeIfAbsent(basePath) { ArrayList() }
        opList.add(co!!)
        co.baseName = basePath
    }

    override fun postProcessAllModels(objects: MutableMap<String, ModelsMap>): MutableMap<String, ModelsMap> {
        super.postProcessAllModels(objects).forEach { obj ->
            obj.value.models.forEach { model: ModelMap ->
                model.model.vars.forEach { variable ->
                    assignDataTypes(
                        variable.dataType,
                        object : DataTypeAssigner {
                            override fun setReturnType(type: String) {
                                variable.dataType = type
                            }

                            override fun setReturnContainer(type: String) {
                                variable.containerType = type
                            }
                        })
                }
            }
        }
        return super.postProcessAllModels(objects)
    }

    override fun postProcessOperationsWithModels(objects: OperationsMap, allModels: List<ModelMap>): OperationsMap {
        objects.operations.operation.forEach { operation ->
            operation.responses.forEach { response ->
                assignDataTypes(
                    response.dataType,
                    object : DataTypeAssigner {
                        override fun setReturnType(type: String) {
                            response.dataType = type
                        }
                        override fun setReturnContainer(type: String) {
                            response.containerType = type
                        }
                    })
            }
            assignDataTypes(
                operation.returnType,
                object : DataTypeAssigner {
                    override fun setReturnType(type: String) {
                        operation.returnType = type
                    }
                    override fun setReturnContainer(type: String) {
                        operation.returnContainer = type
                    }
                })
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

    interface DataTypeAssigner {
        fun setReturnType(type: String)

        fun setReturnContainer(type: String)
    }

    private fun assignDataTypes(type: String?, assigner: DataTypeAssigner) {
        if (type == null) {
            assigner.setReturnType("Unit")
        } else if (type.startsWith("kotlin.collections.List")) {
            val end = type.lastIndexOf(">")
            if (end > 0) {
                assigner.setReturnType(type.substring("kotlin.collections.List<".length, end).trim())
                assigner.setReturnContainer("List")
            }
        }
    }
}
