package com.example.nuki_sesami_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AndroidUriHandler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AboutDialogEntry(
    caption: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${caption}: ")
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AboutDialog(
    onDismissRequest: () -> Unit,
) {
    val version = BuildConfig.VERSION_NAME
    val build = BuildConfig.BUILD_TYPE
    val link = "https://github.com/michelm/nuki-sesami-app"
    val text = stringResource(R.string.about_view_description)
    val annotatedText = LinkedAnnotatedString(
        source = text,
        segment = "nuki-sesami-app",
        link = link,
    ).create()
    val uriHandler = AndroidUriHandler(LocalContext.current)

    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    stringResource(R.string.about_view_description_caption),
                    style = MaterialTheme.typography.titleLarge,
                )
                HorizontalDivider(thickness = 2.dp)
                ClickableText(
                    modifier = Modifier.padding(1.dp),
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium,
                    onClick = {
                        annotatedText
                            .getStringAnnotations(LINK_TAG_URL, it, it)
                            .firstOrNull()
                            ?.let { url -> uriHandler.openUri(url.item) }
                    }
                )

                HorizontalDivider(thickness = 2.dp)
                AboutDialogEntry(stringResource(R.string.about_view_entry_caption_version), version)
                AboutDialogEntry(stringResource(R.string.about_view_entry_caption_build_type), build)
                Spacer(Modifier.padding(5.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}
