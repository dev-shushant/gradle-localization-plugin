package dev.shushant.localization.plugin.utils

import dev.shushant.localization.plugin.models.LocalizationNode
import dev.shushant.localization.plugin.models.ResourceType
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class GenerateTranslations private constructor(builder: Builder) {
    private val mProjDir: File = builder.projDir
    private val mStringsFile: File = File("${mProjDir}$PATH_VALUES$STRINGS_XML")
    private val mDocument: Document?

    class Builder(val projDir: File) {
        fun build(): GenerateTranslations = GenerateTranslations(this)
    }

    init {

        // Copy the original XML file to the "hashes" directory
        val hashDir = getHashDirectory()
        mStringsFile.copyTo(File(hashDir, STRINGS_XML), true)

        mDocument = try {
            val factory = DocumentBuilderFactory.newInstance()
            val dBuilder = factory.newDocumentBuilder()
            FileInputStream(mStringsFile).use { stream ->
                dBuilder.parse(stream)
            }
        } catch (ex: Exception) {
            println("Error creating Document: $ex")
            null
        }
    }

    fun listElements(): List<LocalizationNode> {
        val list = mutableListOf<LocalizationNode>()
        try {
            mDocument?.let { doc ->
                val nodes = doc.documentElement.childNodes
                for (i in 0 until nodes.length) {
                    val element = nodes.item(i) as? Element ?: continue
                    val tagName = element.tagName
                    val name = element.getAttribute("name")

                    when (tagName) {
                        "string" -> {
                            val value = element.textContent
                            val (cleanText, placeholders) = extractPlaceholders(value)
                            list.add(
                                LocalizationNode(
                                    name = name,
                                    originalValue = value,
                                    cleanValue = cleanText,
                                    placeholders = placeholders,
                                    type = ResourceType.STRING
                                )
                            )
                        }

                        "string-array" -> {
                            val items = element.getElementsByTagName("item")
                            for (j in 0 until items.length) {
                                val item = items.item(j) as? Element ?: continue
                                val value = item.textContent
                                val (cleanText, placeholders) = extractPlaceholders(value)
                                list.add(
                                    LocalizationNode(
                                        name = name,
                                        originalValue = value,
                                        cleanValue = cleanText,
                                        placeholders = placeholders,
                                        type = ResourceType.STRING_ARRAY,
                                        index = j
                                    )
                                )
                            }
                        }

                        "plurals" -> {
                            val items = element.getElementsByTagName("item")
                            for (j in 0 until items.length) {
                                val item = items.item(j) as? Element ?: continue
                                val quantity = item.getAttribute("quantity")
                                val value = item.textContent
                                val (cleanText, placeholders) = extractPlaceholders(value)
                                list.add(
                                    LocalizationNode(
                                        name = name,
                                        originalValue = value,
                                        cleanValue = cleanText,
                                        placeholders = placeholders,
                                        type = ResourceType.PLURALS,
                                        quantity = quantity
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            println("Error parsing elements: $ex")
        }
        return list
    }

    fun saveLocalized(lang: String, translatedNodes: List<LocalizationNode>) {
        val localizedValuesDir = File(
            mProjDir, "src${File.separator}main${File.separator}res${File.separator}values-$lang"
        )
        localizedValuesDir.mkdirs()

        val translatedXmlFile = File(localizedValuesDir, STRINGS_XML)
        saveXmlFile(translatedNodes, translatedXmlFile)
    }

    private fun saveXmlFile(nodes: List<LocalizationNode>, outputFile: File) {
        val docFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = docFactory.newDocumentBuilder()

        val doc = docBuilder.newDocument()
        val rootElement = doc.createElement(RESOURCES)
        doc.appendChild(rootElement)

        nodes.forEach { node ->
            when (node.type) {
                ResourceType.STRING -> {
                    val element = doc.createElement("string")
                    element.setAttribute("name", node.name)
                    element.textContent = node.value
                    rootElement.appendChild(element)
                }

                else -> {
                    // Only handling simple "string" case now; you can extend for arrays/plurals later.
                }
            }
        }

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        val source = DOMSource(doc)
        val result = StreamResult(outputFile)
        transformer.transform(source, result)
    }

    // Support functions

    private fun getHashDirectory(): File {
        val buildDir = File(mProjDir, "build")
        buildDir.mkdirs()
        val hashDir = File(buildDir, "hashes")
        hashDir.mkdirs()
        return hashDir
    }

    private fun getHashFile(): File {
        val hashDir = getHashDirectory()
        val hashFileName = "${mStringsFile.nameWithoutExtension}.hash"
        return File(hashDir, hashFileName)
    }

    private fun calculateFileHash(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(8192)
        FileInputStream(file).use { inputStream ->
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun isModified(): Boolean {
        val hashFile = getHashFile()
        val currentHash = calculateFileHash(mStringsFile)

        if (hashFile.exists()) {
            val storedHash = hashFile.readText()
            return storedHash != currentHash
        }
        return true
    }

    fun saveCurrentHash() {
        val hashFile = getHashFile()
        val currentHash = calculateFileHash(mStringsFile)
        hashFile.writeText(currentHash)
    }

    fun extractPlaceholders(value: String): Pair<String, Map<String, String>> {
        val placeholderRegex = Regex("""<xliff:g[^>]+>[^<]+</xliff:g>|%(\d+)\$[sdf]""")
        val matches = placeholderRegex.findAll(value).toList()

        var cleanText = value
        val placeholderMap = mutableMapOf<String, String>()

        matches.forEachIndexed { index, matchResult ->
            val placeholderToken = "__PLACEHOLDER_${index}__"
            placeholderMap[placeholderToken] = matchResult.value
            cleanText = cleanText.replace(matchResult.value, placeholderToken)
        }

        return cleanText to placeholderMap
    }

    companion object {
        val PATH_VALUES =
            "${File.separator}src${File.separator}main${File.separator}res${File.separator}values${File.separator}"
    }
}