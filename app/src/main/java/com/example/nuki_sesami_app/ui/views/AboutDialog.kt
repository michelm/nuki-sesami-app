package com.example.nuki_sesami_app.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.nuki_sesami_app.BuildConfig
import com.example.nuki_sesami_app.ui.misc.LinkedAnnotatedString
import com.example.nuki_sesami_app.R

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
    val version by remember { mutableStateOf(BuildConfig.VERSION_NAME) }
    val build by remember { mutableStateOf(BuildConfig.BUILD_TYPE) }
    val text = stringResource(R.string.about_view_description)
    val url by remember { mutableStateOf("https://github.com/michelm/nuki-sesami-app") }
    val annotatedText = LinkedAnnotatedString(
        source = text,
        segment = "nuki-sesami-app",
        link = url,
    ).create()
    val richText by remember { mutableStateOf(annotatedText) }
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

                HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.padding(2.dp))

                ClickableText(
                    modifier = Modifier.padding(1.dp),
                    text = richText,
                    style = MaterialTheme.typography.bodyMedium,
                    onClick = { uriHandler.openUri(url) }
                )

                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.padding(2.dp))

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

@Preview(showBackground = true)
@Composable
fun AboutDialogPreview() {
    AboutDialog {}
}
