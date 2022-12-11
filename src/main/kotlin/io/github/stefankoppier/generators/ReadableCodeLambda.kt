package io.github.stefankoppier.generators

import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import java.io.Writer

class ReadableCodeLambda : Mustache.Lambda {
    override fun execute(fragment: Template.Fragment, writer: Writer) {
        writer.write(
            when (fragment.execute()) {
                "200" -> "ok"
                "404" -> "notFound"
                else -> throw IllegalArgumentException("Could not map '$fragment' to a http code")
            })
    }
}
