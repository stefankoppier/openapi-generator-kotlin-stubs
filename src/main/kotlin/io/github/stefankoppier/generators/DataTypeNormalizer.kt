package io.github.stefankoppier.generators

import kotlin.reflect.KMutableProperty0
import org.openapitools.codegen.CodegenOperation
import org.openapitools.codegen.CodegenProperty
import org.openapitools.codegen.CodegenResponse

fun CodegenProperty.normalize() {
    normalize(dataType, this::dataType, this::containerType)
}

fun CodegenResponse.normalize() {
    normalize(dataType, this::dataType, this::containerType)
}

fun CodegenOperation.normalize() {
    normalize(returnType, this::returnType, this::returnContainer)
}

internal fun normalize(type: String?, element: KMutableProperty0<String>, container: KMutableProperty0<String>) {
    if (type == null) {
        element.set("Unit")
    } else if (type.startsWith("kotlin.collections.List")) {
        val end = type.lastIndexOf(">")
        if (end > 0) {
            val returnType = type.substring("kotlin.collections.List<".length, end).trim()
            element.set(removePackageName(returnType))
            container.set("List")
        }
    } else {
        element.set(removePackageName(type))
    }
}

private fun removePackageName(type: String): String {
    return if (type.contains('.')) type.substring(type.lastIndexOf('.') + 1).trim() else type
}
