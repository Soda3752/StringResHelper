package org.srtingres.helper.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    var expandedPosition by remember { mutableStateOf(-1) }
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
                    Box {
                        IconButton(
                            onClick = {
                                if ((item.reference?.size ?: 0) > 1) {
                                    // 如果有多筆，顯示下拉選單
                                    expandedPosition = index
                                } else {
                                    // 如果只有一筆或沒有，直接複製
                                    item.reference?.firstOrNull()?.let {
                                        clipboardManager.setText(AnnotatedString(it.key))
                                        // 可選擇顯示複製成功提示
                                        // showToast("已複製鍵值: ${it.key}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Create,
                                contentDescription = "Copy Key",
                                tint = MaterialTheme.colors.primary
                            )
                        }

                        // 下拉選單
                        DropdownMenu(
                            expanded = expandedPosition == index,
                            onDismissRequest = { expandedPosition = -1 },
                            modifier = Modifier.width(IntrinsicSize.Min)
                        ) {
                            item.reference?.forEachIndexed { index, ref ->
                                DropdownMenuItem(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(ref.key))
                                        expandedPosition = -1
                                        // 可選擇顯示複製成功提示
                                        // showToast("已複製鍵值: ${ref.key}")
                                    }
                                ) {
                                    Text("${index + 1}. ${ref.key}")
                                }
                            }
                        }
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
                            Column {
                                item.reference?.let { refList ->
                                    if (refList.size == 1) {
                                        // 只有一筆資料的情況，保持原樣
                                        Row {
                                            Text("Lokalise:")
                                            Text(
                                                text = "<string name=\"${refList[0].key}\">${refList[0].value}</string>",
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                            )
                                        }
                                    } else {
                                        // 多筆資料的情況，使用索引編號
                                        refList.forEachIndexed { index, ref ->
                                            Row {
                                                Text("Lokalise_${index + 1}:")
                                                Text(
                                                    text = "<string name=\"${ref.key}\">${ref.value}</string>",
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
            }
        }
    }
}
