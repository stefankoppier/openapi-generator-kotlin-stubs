package io.github.stefankoppier.generators

fun normalize(type: String?, element: (String) -> Unit, container: (String) -> Unit) {
    if (type == null) {
        element("Unit")
    } else if (type.startsWith("kotlin.collections.List")) {
        val end = type.lastIndexOf(">")
        if (end > 0) {
            val returnType = type.substring("kotlin.collections.List<".length, end).trim()
            element(removePackageName(returnType))
            container("List")
        }
    } else {
        element(removePackageName(type))
    }
}

private fun removePackageName(type: String): String {
    return if (type.contains('.')) type.substring(type.lastIndexOf('.') + 1).trim() else type
}
