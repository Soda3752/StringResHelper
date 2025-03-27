package org.srtingres.helper.model

data class ComparisonItem(
    val modified: StringResource,
    val reference: StringResource?,
    var isChecked: Boolean = false
)