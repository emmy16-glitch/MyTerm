package dev.myterm.ui.terminal

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dev.myterm.domain.terminal.TerminalEngine

/**
 * State of a single terminal session as shown in the UI.
 *
 * `output` currently only records session lifecycle events. Once the real
 * engine lands (Milestone 2) this will carry actual process output — no
 * simulated command results are ever produced here.
 */
data class TerminalSessionUiState(
    val id: Int,
    val title: String,
    val output: List<String>,
)

data class TerminalUiState(
    val sessions: List<TerminalSessionUiState> = emptyList(),
    val activeSessionId: Int? = null,
    /** Honest engine status shown in the status area. */
    val engineStatus: EngineStatus = EngineStatus.NOT_CONNECTED,
) {
    val activeSession: TerminalSessionUiState?
        get() = sessions.firstOrNull { it.id == activeSessionId }

    enum class EngineStatus(val label: String) {
        NOT_CONNECTED("Engine: not connected — shell support lands in Milestone 2"),
        RUNNING("Engine: running"),
        STOPPED("Engine: stopped"),
    }
}

/**
 * Application-logic state holder for the terminal screen.
 *
 * The engine is deliberately not started yet: [TerminalEngine.NotConnected]
 * rejects every start attempt, and submitted commands are acknowledged as
 * "not executed" rather than faked.
 */
class TerminalViewModel : ViewModel() {

    private val engine: TerminalEngine = TerminalEngine.NotConnected

    private val _uiState = mutableStateOf(TerminalUiState())
    val uiState: State<TerminalUiState> = _uiState

    /** Output buffer per session id. */
    private val outputBuffers = mutableMapOf<Int, MutableList<String>>()
    private var nextSessionId = 1

    init {
        createSession()
    }

    fun createSession() {
        val id = nextSessionId++
        outputBuffers[id] = mutableListOf(sessionBanner(id))
        val state = _uiState.value
        _uiState.value = state.copy(
            sessions = state.sessions + TerminalSessionUiState(
                id = id,
                title = "Session $id",
                output = outputBuffers.getValue(id),
            ),
            activeSessionId = id,
        )
    }

    fun closeSession(sessionId: Int) {
        val state = _uiState.value
        outputBuffers.remove(sessionId)
        val remaining = state.sessions
            .filterNot { it.id == sessionId }
            .map { it.copy(output = outputBuffers.getValue(it.id)) }
        if (remaining.isEmpty()) {
            createSession()
            return
        }
        _uiState.value = state.copy(
            sessions = remaining,
            activeSessionId = if (state.activeSessionId == sessionId) remaining.first().id
            else state.activeSessionId,
        )
    }

    fun selectSession(sessionId: Int) {
        _uiState.value = _uiState.value.copy(activeSessionId = sessionId)
    }

    /**
     * A submitted command is reported as accepted-but-not-executed while the
     * engine is not connected. Nothing is echoed as if a shell had run it.
     */
    fun submitCommand(command: String) {
        if (command.isBlank()) return
        val activeId = _uiState.value.activeSessionId ?: return
        val buffer = outputBuffers[activeId] ?: return
        buffer += listOf(
            "$ $command",
            "  (not executed: terminal engine is not connected yet)",
        )
        updateSessionOutput(activeId)
    }

    private fun updateSessionOutput(sessionId: Int) {
        val state = _uiState.value
        _uiState.value = state.copy(
            sessions = state.sessions.map {
                if (it.id == sessionId) it.copy(output = outputBuffers.getValue(sessionId).toList())
                else it
            },
        )
    }

    private fun sessionBanner(id: Int): String =
        "Session $id ready — terminal engine not connected (Milestone 2 pending)"
}
