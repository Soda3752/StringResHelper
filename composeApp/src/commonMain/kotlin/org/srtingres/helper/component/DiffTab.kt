package org.srtingres.helper.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.srtingres.helper.model.StringResource
import org.srtingres.helper.resources.Res
import org.srtingres.helper.resources.ic_copy
import org.srtingres.helper.resources.ic_key

@Composable
fun DiffTab(
    modifiedMissingItems: List<StringResource>,
    referenceMissingItems: List<StringResource>
) {
    val clipboardManager = LocalClipboardManager.current
    var checkedModifiedItems by remember { mutableStateOf<Set<String>>(setOf()) }
    var checkedReferenceItems by remember { mutableStateOf<Set<String>>(setOf()) }

    // 新增排序函數
    fun List<StringResource>.sortByChecked(checkedItems: Set<String>): List<StringResource> {
        return sortedWith(compareBy<StringResource> { checkedItems.contains(it.key) }
            .thenBy { it.key })
    }

    // 對列表進行排序
    val sortedModifiedItems = remember(modifiedMissingItems, checkedModifiedItems) {
        modifiedMissingItems.sortByChecked(checkedModifiedItems)
    }

    val sortedReferenceItems = remember(referenceMissingItems, checkedReferenceItems) {
        referenceMissingItems.sortByChecked(checkedReferenceItems)
    }

    @Composable
    fun ResourceItem(
        item: StringResource,
        isChecked: Boolean,
        onCheckedChange: (Boolean) -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 2.dp,
            backgroundColor = if (isChecked)
                MaterialTheme.colors.surface.copy(alpha = 0.5f)
            else MaterialTheme.colors.surface
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange
                )

                Column {
                    IconButton(
                        modifier = Modifier.size(24.dp),
                        onClick = {
                            clipboardManager.setText(AnnotatedString(item.key))
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.ic_key),
                            contentDescription = "Copy Key",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                    IconButton(
                        modifier = Modifier.size(24.dp),
                        onClick = {
                            clipboardManager.setText(AnnotatedString(item.value))
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.ic_copy),
                            contentDescription = "Copy Value",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }


                SelectionContainer {
                    Row(Modifier.padding(8.dp)) {
                        // 標題列
                        Column(
                            modifier = Modifier.width(IntrinsicSize.Max).padding(end = 8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Key:",
                                style = MaterialTheme.typography.subtitle1,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "Value:",
                                style = MaterialTheme.typography.subtitle1
                            )
                        }

                        // 內容列
                        Column {
                            Text(
                                text = item.key,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = item.value,
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                }
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 左側列表
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Missing in Modified (${modifiedMissingItems.size})",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedModifiedItems) { item ->
                    ResourceItem(
                        item = item,
                        isChecked = checkedModifiedItems.contains(item.key),
                        onCheckedChange = { checked ->
                            checkedModifiedItems = if (checked) {
                                checkedModifiedItems + item.key
                            } else {
                                checkedModifiedItems - item.key
                            }
                        }
                    )
                }
            }
        }

        // 右側列表
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Missing in Reference (${referenceMissingItems.size})",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedReferenceItems) { item ->
                    ResourceItem(
                        item = item,
                        isChecked = checkedReferenceItems.contains(item.key),
                        onCheckedChange = { checked ->
                            checkedReferenceItems = if (checked) {
                                checkedReferenceItems + item.key
                            } else {
                                checkedReferenceItems - item.key
                            }
                        }
                    )
                }
            }
        }
    }
} 