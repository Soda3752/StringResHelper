package org.srtingres.helper

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.srtingres.helper.component.DiffTab
import org.srtingres.helper.component.InputTab
import org.srtingres.helper.component.MissTab
import org.srtingres.helper.model.*

@Composable
fun App() {
    var modifiedText by remember { mutableStateOf("") }
    var referenceText by remember { mutableStateOf("") }
    var filterPrefixText by remember { mutableStateOf("") }
    var comparisonItems by remember { mutableStateOf<List<ComparisonItem>>(emptyList()) }
    var parseError by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var checkKeyFormat by remember { mutableStateOf(true) }
    var isIosMode by remember { mutableStateOf(false) }
    var modifiedMissingItems by remember { mutableStateOf<List<StringResource>>(emptyList()) }
    var referenceMissingItems by remember { mutableStateOf<List<StringResource>>(emptyList()) }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Input") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Diff") }
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = { Text("Missing") }
                    )
                }

                when (selectedTabIndex) {
                    0 -> InputTab(
                        modifiedText = modifiedText,
                        onModifiedTextChange = { modifiedText = it },
                        referenceText = referenceText,
                        onReferenceTextChange = { referenceText = it },
                        filterPrefixText = filterPrefixText,
                        onFilterPrefixTextChange = { filterPrefixText = it },
                        isIosMode = isIosMode,
                        onIosModeChange = { isIosMode = it },
                        checkKeyFormat = checkKeyFormat,
                        onCheckKeyFormatChange = { checkKeyFormat = it },
                        onProcess = {
                            try {
                                val modifiedResources = if (isIosMode) {
                                    parseIosStringResources(modifiedText, filterPrefixText)
                                } else {
                                    parseStringResources(modifiedText, checkKeyFormat, filterPrefixText)
                                }

                                val referenceResources = parseStringResources(referenceText, checkKeyFormat)

                                comparisonItems = compareResources(modifiedResources, referenceResources)

                                // 計算缺少的項目
                                modifiedMissingItems = referenceResources.filter { ref ->
                                    modifiedResources.none { it.key == ref.key }
                                }
                                referenceMissingItems = modifiedResources.filter { mod ->
                                    referenceResources.none { it.key == mod.key }
                                }

                                parseError = ""
                                selectedTabIndex = 1
                            } catch (e: Exception) {
                                parseError = "Parse Error: ${e.message}"
                                comparisonItems = emptyList()
                                modifiedMissingItems = emptyList()
                                referenceMissingItems = emptyList()
                                selectedTabIndex = 1
                            }
                        }
                    )

                    1 -> DiffTab(
                        comparisonItems = comparisonItems,
                        parseError = parseError,
                        onCheckedChange = { index, checked ->
                            comparisonItems = comparisonItems.toMutableList().apply {
                                this[index] = this[index].copy(isChecked = checked)
                            }.sortedWith(compareBy({ it.isChecked }, { it.modified.key }))
                        }
                    )

                    2 -> MissTab(
                        modifiedMissingItems = modifiedMissingItems,
                        referenceMissingItems = referenceMissingItems
                    )
                }
            }
        }
    }
}
