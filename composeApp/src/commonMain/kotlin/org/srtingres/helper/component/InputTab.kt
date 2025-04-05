package org.srtingres.helper.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InputTab(
    modifiedText: String,
    onModifiedTextChange: (String) -> Unit,
    referenceText: String,
    onReferenceTextChange: (String) -> Unit,
    filterPrefixText: String,
    onFilterPrefixTextChange: (String) -> Unit,
    isIosMode: Boolean,
    onIosModeChange: (Boolean) -> Unit,
    checkKeyFormat: Boolean,
    onCheckKeyFormatChange: (Boolean) -> Unit,
    onProcess: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = modifiedText,
                onValueChange = onModifiedTextChange,
                label = { Text("Modify Res") },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface
                ),
                maxLines = Int.MAX_VALUE
            )

            TextField(
                value = referenceText,
                onValueChange = onReferenceTextChange,
                label = { Text("Lokalise Res") },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface
                ),
                maxLines = Int.MAX_VALUE
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (isIosMode) "iOS Mode" else "Android Mode")
                Switch(isIosMode, onCheckedChange = onIosModeChange)
            }

            OutlinedTextField(
                value = filterPrefixText,
                onValueChange = onFilterPrefixTextChange,
                label = { Text("Filter Prefix") },
                modifier = Modifier,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface
                ),
                maxLines = 1
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Check Key Format")
                Checkbox(
                    checked = checkKeyFormat,
                    onCheckedChange = onCheckKeyFormatChange,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Button(onClick = onProcess) {
                Text("Process")
            }
        }
    }
} 