package dev.shushant.localization.plugin.models


enum class ResourceType {
    STRING,
    STRING_ARRAY,
    PLURALS
}


data class LocalizationNode(
    val name: String,
    val originalValue: String,
    val cleanValue: String,
    val isPlural: Boolean = false,
    val quantity: String? = null, // only for plurals
    val index: Int? = null,       // only for string-array
    val type: ResourceType,
    val value: String = originalValue, // default to original value
    val placeholders: Map<String, String> = emptyMap() // new field
)