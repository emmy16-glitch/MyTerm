package dev.myterm.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.myterm.ui.terminal.TerminalSessionUiState

/**
 * Horizontal session switcher: one chip per session plus a "new session"
 * button. Each chip is followed by an independent close button so closing
 * a session never conflicts with selecting it.
 */
@Composable
fun SessionTabs(
    sessions: List<TerminalSessionUiState>,
    activeSessionId: Int?,
    onSelect: (Int) -> Unit,
    onClose: (Int) -> Unit,
    onNewSession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        sessions.forEach { session ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = session.id == activeSessionId,
                    onClick = { onSelect(session.id) },
                    label = { Text(session.title) },
                )
                if (sessions.size > 1) {
                    IconButton(
                        onClick = { onClose(session.id) },
                        modifier = Modifier.padding(start = 2.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close ${session.title}",
                        )
                    }
                }
            }
        }
        IconButton(onClick = onNewSession) {
            Icon(Icons.Filled.Add, contentDescription = "New session")
        }
    }
}
