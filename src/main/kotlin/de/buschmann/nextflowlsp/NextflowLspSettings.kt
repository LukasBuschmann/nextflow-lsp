package de.buschmann.nextflowlsp

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@Service(Service.Level.APP)
@State(name = "NextflowLspSettings", storages = [Storage("nextflow-lsp.xml")])
internal class NextflowLspSettings : PersistentStateComponent<NextflowLspSettings.State> {

    data class State(
        var serverJarPath: String? = null,
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    fun setServerJarPath(path: String?) {
        state = state.copy(serverJarPath = path)
    }

    companion object {
        fun getInstance(): NextflowLspSettings = ApplicationManager.getApplication().service()
    }
}
