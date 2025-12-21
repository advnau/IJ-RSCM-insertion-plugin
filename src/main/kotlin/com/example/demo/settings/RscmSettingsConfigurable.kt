package com.example.demo.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import java.nio.file.Paths
import javax.swing.JComponent

class RscmSettingsConfigurable : Configurable {
    private val settings: RscmSettingsState = RscmSettingsState.getInstance()
    private var rscmPath: String = settings.rscmDirectory.ifBlank { RscmSettingsState.defaultPath() }
    private var pathField: TextFieldWithBrowseButton? = null

    override fun getDisplayName(): String = "RSCM Import Settings"

    override fun createComponent(): JComponent {
        val chooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
        pathField = TextFieldWithBrowseButton()
        pathField?.text = rscmPath
        pathField?.addBrowseFolderListener(
            "Select RSCM folder",
            "Pick the directory that contains your .rscm files.",
            null,
            chooserDescriptor
        )

        return panel {
            row("RSCM directory:") {
                cell(pathField!!)
                    .bindText(::rscmPath)
                    .columns(40)
            }
        }
    }

    override fun isModified(): Boolean = rscmPath.trim() != settings.rscmDirectory

    override fun apply() {
        val normalized = Paths.get(rscmPath.trim()).toAbsolutePath().normalize().toString()
        settings.rscmDirectory = normalized
    }

    override fun reset() {
        rscmPath = settings.rscmDirectory
        pathField?.text = rscmPath
    }
}
