package com.noor.app.ui.screens.memorize

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.noor.app.R
import com.noor.app.ui.components.NoorCard
import com.noor.app.ui.components.ScreenScaffold
import com.noor.app.ui.components.SectionTitle

private enum class MemorizeMode { NEW, REVIEW, TASMI }

@Composable
fun MemorizeScreen(onBack: () -> Unit) {
    var mode by rememberSaveable { mutableStateOf(MemorizeMode.NEW.name) }
    var surah by rememberSaveable { mutableStateOf("") }
    var fromPage by rememberSaveable { mutableStateOf("1") }
    var toPage by rememberSaveable { mutableStateOf("1") }
    var started by rememberSaveable { mutableStateOf(false) }
    var completedPages by rememberSaveable { mutableIntStateOf(0) }

    val start = fromPage.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val end = toPage.toIntOrNull()?.coerceAtLeast(start) ?: start
    val totalPages = (end - start + 1).coerceAtMost(604)
    val progress = (completedPages.toFloat() / totalPages.coerceAtLeast(1)).coerceIn(0f, 1f)

    ScreenScaffold(
        title = stringResource(R.string.section_memorize),
        onBack = onBack
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NoorCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.memorize_intro_title), style = MaterialTheme.typography.titleLarge)
                    Text(
                        stringResource(R.string.memorize_intro_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            SectionTitle(stringResource(R.string.memorize_mode_title))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = mode == MemorizeMode.NEW.name,
                    onClick = { if (!started) mode = MemorizeMode.NEW.name },
                    label = { Text(stringResource(R.string.memorize_mode_new)) }
                )
                FilterChip(
                    selected = mode == MemorizeMode.REVIEW.name,
                    onClick = { if (!started) mode = MemorizeMode.REVIEW.name },
                    label = { Text(stringResource(R.string.memorize_mode_review)) }
                )
                FilterChip(
                    selected = mode == MemorizeMode.TASMI.name,
                    onClick = { if (!started) mode = MemorizeMode.TASMI.name },
                    label = { Text(stringResource(R.string.memorize_mode_tasmi)) }
                )
            }

            NoorCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.memorize_plan_title), style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = surah,
                        onValueChange = { if (!started) surah = it.take(40) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.memorize_surah_label)) },
                        placeholder = { Text(stringResource(R.string.memorize_surah_hint)) },
                        singleLine = true,
                        enabled = !started
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = fromPage,
                            onValueChange = { if (!started) fromPage = it.filter(Char::isDigit).take(3) },
                            modifier = Modifier.weight(1f),
                            label = { Text(stringResource(R.string.memorize_from_page)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            enabled = !started
                        )
                        OutlinedTextField(
                            value = toPage,
                            onValueChange = { if (!started) toPage = it.filter(Char::isDigit).take(3) },
                            modifier = Modifier.weight(1f),
                            label = { Text(stringResource(R.string.memorize_to_page)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            enabled = !started
                        )
                    }
                    Text(
                        stringResource(R.string.memorize_pages_count, totalPages),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!started) {
                        Button(
                            onClick = { started = true; completedPages = 0 },
                            enabled = surah.isNotBlank() && (fromPage.toIntOrNull() ?: 0) in 1..604 &&
                                (toPage.toIntOrNull() ?: 0) in start..604,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(stringResource(R.string.memorize_start)) }
                    } else {
                        Text(
                            stringResource(R.string.memorize_session_active, surah),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                        Text(
                            stringResource(R.string.memorize_progress, completedPages, totalPages),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = { completedPages = (completedPages + 1).coerceAtMost(totalPages) },
                            enabled = completedPages < totalPages,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(stringResource(R.string.memorize_page_done)) }
                        OutlinedButton(
                            onClick = { started = false },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(stringResource(R.string.memorize_finish)) }
                    }
                }
            }

            NoorCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.memorize_tasmi_note_title), style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(R.string.memorize_tasmi_note_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
