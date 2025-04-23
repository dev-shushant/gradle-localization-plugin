package dev.shushant.localization.plugin.models


data class LocalizationNode(
    val name:String = "",
    val value:String = "",
    val type: NodeType = NodeType.STRING,
    val cleanValue: String,      // Text with placeholders removed or masked for translation
    val placeholders: List<String> = emptyList(), // %1$s etc.
    val isPlural: Boolean = false,
    val quantity: String? = null // one, few, many, other etc. (only for plurals)
)

enum class NodeType {
    STRING,
    STRING_ARRAY
}