package de.buschmann.nextflowlsp

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

internal class NextflowLspSettingsConfigurable : Configurable {

    private var panel: JPanel? = null
    private var serverJarField: TextFieldWithBrowseButton? = null

    override fun getDisplayName(): String = "Nextflow LSP"

    override fun createComponent(): JComponent {
        if (panel != null) return panel as JPanel

        val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("jar")
        serverJarField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                "Select Nextflow LSP JAR",
                "Choose the Nextflow language server JAR file.",
                null,
                descriptor,
            )
        }

        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Language Server JAR"), serverJarField!!)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        return panel as JPanel
    }

    override fun isModified(): Boolean {
        val settings = NextflowLspSettings.getInstance().state
        return serverJarField?.text?.trim().orEmpty() != (settings.serverJarPath ?: "")
    }

    override fun apply() {
        val settings = NextflowLspSettings.getInstance()
        settings.setServerJarPath(serverJarField?.text?.trim().orEmpty().ifEmpty { null })
    }

    override fun reset() {
        val settings = NextflowLspSettings.getInstance().state
        serverJarField?.text = settings.serverJarPath.orEmpty()
    }

    override fun disposeUIResources() {
        panel = null
        serverJarField = null
    }
}
