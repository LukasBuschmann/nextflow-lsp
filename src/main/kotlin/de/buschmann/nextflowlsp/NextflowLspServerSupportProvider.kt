package de.buschmann.nextflowlsp

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.LspServerManager
import com.intellij.platform.lsp.api.LspServerState
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.util.concurrency.AppExecutorUtil
import org.eclipse.lsp4j.DidChangeConfigurationParams
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

internal class NextflowLspServerSupportProvider : LspServerSupportProvider {

    override fun fileOpened(
        project: Project,
        file: VirtualFile,
        serverStarter: LspServerSupportProvider.LspServerStarter,
    ) {
        if (file.extension == "nf") {
            serverStarter.ensureServerStarted(NextflowLspServerDescriptor(project))
            scheduleInitialConfiguration(project)
        }
    }

    private fun scheduleInitialConfiguration(project: Project) {
        AppExecutorUtil.getAppExecutorService().submit {
            repeat(25) {
                if (tryConfigureServers(project)) return@submit
                TimeUnit.MILLISECONDS.sleep(200)
            }
        }
    }

    private fun tryConfigureServers(project: Project): Boolean {
        val manager = LspServerManager.getInstance(project)
        var configuredAny = false
        for (server in manager.getServersForProvider(NextflowLspServerSupportProvider::class.java)) {
            if (server.state != LspServerState.Running) continue
            val key = serverKey(server)
            if (!configuredServers.add(key)) continue
            sendInitialConfiguration(server)
            configuredAny = true
        }
        return configuredAny
    }

    private fun sendInitialConfiguration(server: LspServer) {
        val settings = mapOf(
            "nextflow" to mapOf(
                "typeChecking" to true,
            )
        )
        server.sendNotification { it.workspaceService.didChangeConfiguration(DidChangeConfigurationParams(settings)) }
    }

    private fun serverKey(server: LspServer): String =
        "${server.project.locationHash}:${server.descriptor}"

    private companion object {
        private val configuredServers = ConcurrentHashMap.newKeySet<String>()
    }
}
