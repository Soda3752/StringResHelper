package org.srtingres.helper.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Create
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import org.srtingres.helper.model.ComparisonItem


@Composable
fun ComparisonResultList(
    items: List<ComparisonItem>,
    onCheckedChange: (Int, Boolean) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    LazyColumn {
        items(items.size) { index ->
            val item = items[index]
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                elevation = 2.dp,
                backgroundColor = if (item.isChecked) MaterialTheme.colors.surface.copy(alpha = 0.5f) else MaterialTheme.colors.surface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Checkbox
                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = { checked ->
                            onCheckedChange(index, checked)
                        }
                    )

                    // Copy Key Button
                    IconButton(
                        onClick = {
                            item.reference?.let {
                                clipboardManager.setText(AnnotatedString(it.key))

                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Create,
                            contentDescription = "Copy Key",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                    SelectionContainer() {
                        Column(Modifier.padding(8.dp)) {
                            Text("At Line: ${item.modified.atLine}")



                            Row {
                                Text("Current: ")
                                Text(
                                    text = "<string name=\"${item.modified.key}\">${item.modified.value}</string>",
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }

                            Row {
                                Text("Lokalise:")
                                // Reference string
                                Text(
                                    text = item.reference?.let {
                                        "<string name=\"${it.key}\">${it.value}</string>"
                                    } ?: "",
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }

                        }
                    }

                }
            }
        }
    }
}
