package org.srtingres.helper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.srtingres.helper.component.ComparisonResultList
import org.srtingres.helper.model.ComparisonItem
import org.srtingres.helper.model.compareResources
import org.srtingres.helper.model.lokalise
import org.srtingres.helper.model.originTest
import org.srtingres.helper.model.parseStringResources

@Composable
fun App() {
    var modifiedText by remember { mutableStateOf(originTest) }
    var referenceText by remember { mutableStateOf(lokalise) }
    var comparisonItems by remember { mutableStateOf<List<ComparisonItem>>(emptyList()) }
    var parseError by remember { mutableStateOf("") }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 輸入區塊
                Row(
                    modifier = Modifier.fillMaxWidth().weight(0.5f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 修改檔字串
                    TextField(
                        value = modifiedText,
                        onValueChange = { modifiedText = it },
                        label = { Text("Android String Res") },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = MaterialTheme.colors.surface
                        ),
                        maxLines = Int.MAX_VALUE
                    )

                    // 參考字串
                    TextField(
                        value = referenceText,
                        onValueChange = { referenceText = it },
                        label = { Text("Lokalise Res") },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = MaterialTheme.colors.surface
                        ),
                        maxLines = Int.MAX_VALUE
                    )
                }

                // 執行按鈕
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            try {
                                val modifiedResources = parseStringResources(modifiedText)
                                val referenceResources = parseStringResources(referenceText)

                                comparisonItems = compareResources(modifiedResources, referenceResources)
                                parseError = ""
                            } catch (e: Exception) {
                                parseError = "Parse Error: ${e.message}"
                                comparisonItems = emptyList()
                            }
                        }
                    ) {
                        Text("Process")
                    }
                }

                // 結果區塊
                Column(
                    modifier = Modifier.fillMaxWidth().weight(0.5f)
                ) {
                    Text(
                        text = "Process Result:",
                        style = MaterialTheme.typography.h6
                    )

                    if (parseError.isNotEmpty()) {
                        Text(
                            text = parseError,
                            color = MaterialTheme.colors.error,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    ComparisonResultList(
                        items = comparisonItems,
                        onCheckedChange = { index, checked ->
                            comparisonItems = comparisonItems.toMutableList().apply {
                                this[index] = this[index].copy(isChecked = checked)
                            }.sortedWith(compareBy({ it.isChecked }, { it.modified.key }))
                        }
                    )
                }
            }
        }
    }
}

