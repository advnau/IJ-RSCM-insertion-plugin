package com.example.demo.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import java.nio.file.Paths

@State(name = "RscmSettings", storages = [Storage("rscmSettings.xml")])
@Service(Service.Level.APP)
class RscmSettingsState : PersistentStateComponent<RscmSettingsState.State> {
    companion object {
        fun getInstance(): RscmSettingsState = service()

        fun defaultPath(): String {
            return Paths.get(System.getProperty("user.home"), "rscm").toString()
        }
    }

    data class State(var rscmDirectory: String = defaultPath())

    private var state: State = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var rscmDirectory: String
        get() = state.rscmDirectory
        set(value) {
            state.rscmDirectory = value.trim()
        }
}
