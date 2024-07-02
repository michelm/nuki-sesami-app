package com.example.nuki_sesami_app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

const val LINK_TAG_URL = "ANNOTATION_TAG_URL"

class LinkedAnnotatedString(
    private val source: String,
    private val segment: String,
    private val link: String
) {
    @Composable
    fun create(): AnnotatedString {
        val tagStart = source.indexOf(segment) // start of span marked by 'segment'
        val tagEnd = tagStart + segment.length // end of span marked by 'segment'
        val builder = AnnotatedString.Builder()
        builder.append(source) // load current text into the builder

        builder.addStyle( // prefix
            SpanStyle(color = MaterialTheme.colorScheme.onSurface),
            0,
            tagStart
        )

        builder.addStyle( // segment
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
            ),
            tagStart,
            tagEnd
        )

        builder.addStyle( // postfix
            SpanStyle(color = MaterialTheme.colorScheme.onSurface),
            tagEnd,
            source.length
        )

        builder.addStringAnnotation(
            LINK_TAG_URL, // link can be accessed using this tag
            link,
            tagStart,
            tagEnd
        )

        return builder.toAnnotatedString()
    }
}
