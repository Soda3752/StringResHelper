package org.srtingres.helper.model

data class ComparisonItem(
    val modified: StringResource,
    val reference: MutableList<StringResource>?,
    var isChecked: Boolean = false
)