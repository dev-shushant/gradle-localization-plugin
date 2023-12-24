package dev.shushant.localization.plugin.utils

import dev.shushant.localization.plugin.models.LocalizationNode
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class GenerateTranslations private constructor(builder: Builder) {
    private val mProjDir: File
    private val mStringsFile: File
    private val mOriginalXmlFile: File
    private val mDocument: Document?
    private val mInputStream: InputStream?

    class Builder(val projDir: File) {
        fun build(): GenerateTranslations = GenerateTranslations(this)
    }

    init {
        mProjDir = builder.projDir
        mStringsFile = File("${mProjDir}$PATH_VALUES$STRINGS_XML")
        mOriginalXmlFile = File("${mProjDir}$PATH_VALUES$STRINGS_XML")

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
            println("Error creating LDocument: $ex")
            null
        }
        mInputStream = null
    }

    fun listElements(): List<LocalizationNode> {
        val list = mutableListOf<LocalizationNode>()
        try {
            mDocument?.let { doc ->
                val nodes = doc.documentElement.childNodes
                list.addAll((0 until nodes.length).mapNotNull { nodes.item(it) as? Element }.map {
                    val name = it.getAttribute("name")
                    val value = it.textContent
                    LocalizationNode(name, value)
                })
            }
        } catch (ex: Exception) {
            println("Error listing elements: $ex")
        }
        return list
    }

    // Modification detection
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
        saveOriginalXml()
    }

    private fun getHashFile(): File {
        val hashDir = getHashDirectory()
        val hashFileName = "${mStringsFile.nameWithoutExtension}.hash"
        return File(hashDir, hashFileName)
    }

    private fun getHashDirectory(): File {
        val buildDir = File(mProjDir, "build")
        buildDir.mkdirs()

        val hashDir = File(buildDir, "hashes")
        hashDir.mkdirs()
        return hashDir
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
        return digest.digest().encodeHex()
    }

    private fun saveOriginalXml() {
        val hashDir = getHashDirectory()
        val originalXmlFile = File(hashDir, STRINGS_XML)
        mStringsFile.copyTo(originalXmlFile, true)
    }

    private fun ByteArray.encodeHex(): String {
        return joinToString("") { "%02x".format(it) }
    }

    //Save localized file
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
            val element = doc.createElement(STRING_ELEMENT)
            element.setAttribute("name", node.name)
            element.textContent = node.value
            rootElement.appendChild(element)
        }
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        val source = DOMSource(doc)
        val result = StreamResult(outputFile)
        transformer.transform(source, result)
    }
    //End save localized file

    companion object {
        val PATH_VALUES =
            "${File.separator}src${File.separator}main${File.separator}res${File.separator}values${File.separator}"
    }
}