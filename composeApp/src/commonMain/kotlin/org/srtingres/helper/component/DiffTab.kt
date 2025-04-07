package org.srtingres.helper.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.srtingres.helper.model.ComparisonItem

@Composable
fun DiffTab(
    comparisonItems: List<ComparisonItem>,
    parseError: String,
    onCheckedChange: (Int, Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Process Result: Still Have ${comparisonItems.filter { it.isChecked.not() }.size} items",
            style = MaterialTheme.typography.h6
        )

        if (parseError.isNotEmpty()) {
            Text(
                text = parseError,
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            ComparisonResultList(
                items = comparisonItems,
                onCheckedChange = onCheckedChange
            )
        }
    }
} 