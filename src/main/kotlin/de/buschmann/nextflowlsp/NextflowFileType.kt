package de.buschmann.nextflowlsp

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object NextflowLanguage : com.intellij.lang.Language("Nextflow")

object NextflowFileType : LanguageFileType(NextflowLanguage) {
    override fun getName() = "Nextflow"
    override fun getDescription() = "Nextflow pipeline script"
    override fun getDefaultExtension() = "nf"
    override fun getIcon(): Icon? = NextflowIcons.FILE
}
