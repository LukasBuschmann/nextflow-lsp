package de.buschmann.nextflowlsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor

private const val DEFAULT_NEXTFLOW_LSP_JAR = "/home"
private const val LSP_JAR_PROPERTY = "nextflow.lsp.jar"
private const val LSP_JAR_ENV = "NEXTFLOW_LSP_JAR"

internal class NextflowLspServerDescriptor(project: Project) :
    ProjectWideLspServerDescriptor(project, "Nextflow LSP") {

    override fun isSupportedFile(file: VirtualFile): Boolean =
        file.extension == "nf"

    override fun getLanguageId(file: VirtualFile): String = "nextflow"

    override fun createCommandLine(): GeneralCommandLine {
        val jarPath = resolveJarPath()
        return GeneralCommandLine("java", "-jar", jarPath)
    }
}

private fun resolveJarPath(): String {
    val fromProp = System.getProperty(LSP_JAR_PROPERTY)?.trim().orEmpty()
    if (fromProp.isNotEmpty()) return fromProp

    val fromEnv = System.getenv(LSP_JAR_ENV)?.trim().orEmpty()
    if (fromEnv.isNotEmpty()) return fromEnv

    val fromSettings = NextflowLspSettings.getInstance().state.serverJarPath?.trim().orEmpty()
    if (fromSettings.isNotEmpty()) return fromSettings

    return DEFAULT_NEXTFLOW_LSP_JAR
}
