package com.example.nuki_sesami_app

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration

const val LINK_TAG_URL = "ANNOTATION_TAG_URL"

class LinkedAnnotatedString(
    private val source: String,
    private val segment: String,
    private val link: String
) {
    fun create(): AnnotatedString {
        val builder = AnnotatedString.Builder()
        builder.append(source) // load current text into the builder
        val start = source.indexOf(segment) // start of span marked by 'segment'
        val end = start + segment.length // end of span marked by 'segment'

        builder.addStyle(
            SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
            ),
            start,
            end
        )

        builder.addStringAnnotation(
            LINK_TAG_URL, // link can be accessed using this tag
            link,
            start,
            end
        )

        return builder.toAnnotatedString()
    }
}
