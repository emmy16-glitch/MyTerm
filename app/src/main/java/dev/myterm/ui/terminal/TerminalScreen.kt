package dev.myterm.ui.terminal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.myterm.R
import dev.myterm.ui.components.SessionTabs
import dev.myterm.ui.components.StatusPill
import dev.myterm.ui.theme.TerminalTextStyle

/**
 * Main terminal screen: output region, command input, session controls and
 * status area. The engine is not connected yet; the UI says so explicitly
 * and never simulates command output.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    state: TerminalUiState,
    onCommandSubmitted: (String) -> Unit,
    onNewSession: () -> Unit,
    onCloseSession: (Int) -> Unit,
    onSelectSession: (Int) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                actions = {
                    // Settings entry point. The settings screen itself is a
                    // later milestone; the control exists as the navigation
                    // anchor.
                    IconButton(onClick = { /* Milestone 1+: navigation hook */ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            StatusPill(text = state.engineStatus.label)

            SessionTabs(
                sessions = state.sessions,
                activeSessionId = state.activeSessionId,
                onSelect = onSelectSession,
                onClose = onCloseSession,
                onNewSession = onNewSession,
            )

            TerminalOutput(
                output = state.activeSession?.output.orEmpty(),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )

            CommandInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                enabled = state.engineStatus == TerminalUiState.EngineStatus.RUNNING,
                onSubmit = onCommandSubmitted,
            )
        }
    }
}

@Composable
private fun TerminalOutput(
    output: List<String>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(output.size) {
        if (output.isNotEmpty()) listState.animateScrollToItem(output.size - 1)
    }
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(output) { line ->
            Text(
                text = line,
                style = TerminalTextStyle,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
private fun CommandInput(
    enabled: Boolean,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by rememberSaveable { mutableStateOf("") }

    fun submit() {
        if (!enabled || text.isBlank()) return
        onSubmit(text)
        text = ""
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            enabled = enabled,
            singleLine = true,
            placeholder = {
                Text(if (enabled) "Enter command" else "Waiting for terminal engine")
            },
        )
        IconButton(onClick = ::submit, enabled = enabled && text.isNotBlank()) {
            Icon(Icons.Filled.Close, contentDescription = "Send")
        }
    }
}
