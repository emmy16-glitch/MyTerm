package dev.myterm.domain.terminal

/**
 * Contract for the MyTerm terminal engine.
 *
 * This interface is the boundary between the UI and the terminal backend.
 * Milestone 2 will provide the real implementation (PTY + terminal
 * emulation). Until then, [NotConnectedEngine] is the only implementation
 * and the UI reports honestly that the engine is not connected.
 *
 * Design notes:
 * - The engine must never depend on Android UI classes.
 * - Implementations own the process/PTY lifecycle; the UI only observes.
 */
interface TerminalEngine {

    /** True while a shell/process is attached and running. */
    val isRunning: Boolean

    /**
     * Start the engine and attach it to a shell. Returns false if the
     * engine could not start; [error] then describes why.
     */
    suspend fun start(): Boolean

    /** Send a line of user input to the running process. */
    fun write(line: String)

    /** Terminate the attached process and release resources. */
    fun stop()

    companion object {
        /** Placeholder used until Milestone 2 wires the real engine. */
        val NotConnected: TerminalEngine = NotConnectedEngine
    }
}

private object NotConnectedEngine : TerminalEngine {
    override val isRunning: Boolean = false
    override suspend fun start(): Boolean = false
    override fun write(line: String) = Unit
    override fun stop() = Unit
}
