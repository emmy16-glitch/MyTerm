package dev.myterm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.myterm.ui.terminal.TerminalScreen
import dev.myterm.ui.terminal.TerminalViewModel
import dev.myterm.ui.theme.MyTermTheme

/**
 * Single-activity entry point. All UI is Jetpack Compose; navigation to
 * additional screens (settings) is added as those screens become real.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyTermTheme {
                val viewModel: TerminalViewModel = viewModel()
                val state by viewModel.uiState
                TerminalScreen(
                    state = state,
                    onCommandSubmitted = viewModel::submitCommand,
                    onNewSession = viewModel::createSession,
                    onCloseSession = viewModel::closeSession,
                    onSelectSession = viewModel::selectSession,
                )
            }
        }
    }
}
