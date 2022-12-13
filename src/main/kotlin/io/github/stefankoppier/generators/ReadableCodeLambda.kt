package io.github.stefankoppier.generators

import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import java.io.Writer

class ReadableCodeLambda : Mustache.Lambda {
    override fun execute(fragment: Template.Fragment, writer: Writer) {
        writer.write(map(fragment.execute()))
    }

    private fun map(code: String): String {
        return when (code) {
            "200" -> "ok"
            "201" -> "created"
            "204" -> "noContent"
            "400" -> "badRequest"
            "401" -> "unauthorized"
            "403" -> "forbidden"
            "404" -> "notFound"
            "500" -> "serverError"
            "503" -> "serviceUnavailable"
            else -> throw IllegalArgumentException("Could not map 'code' to a http code")
        }
    }
}
