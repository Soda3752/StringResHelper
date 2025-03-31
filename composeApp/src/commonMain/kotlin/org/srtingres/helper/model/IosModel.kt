package org.srtingres.helper.model

import kotlinx.serialization.Serializable


@Serializable
data class StringResourceFile(
    val sourceLanguage: String,
    val strings: Map<String, StringEntry>,
    val version: String
)

@Serializable
data class StringEntry(
    val extractionState: String? = null,
    val localizations: Map<String, Localization>? = null
)

@Serializable
data class Localization(
    val stringUnit: StringUnit? = null
)

@Serializable
data class StringUnit(
    val state: String? = null,
    val value: String? = null
)