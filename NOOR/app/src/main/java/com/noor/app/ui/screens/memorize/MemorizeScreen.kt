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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.noor.app.R
import com.noor.app.data.memorize.MemorizeProgress
import com.noor.app.data.memorize.MemorizeRepository
import com.noor.app.ui.components.NoorCard
import com.noor.app.ui.components.ScreenScaffold
import com.noor.app.ui.components.SectionTitle
import kotlinx.coroutines.launch

private enum class MemorizeMode { NEW, REVIEW, TASMI }

@Composable
fun MemorizeScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember(context) { MemorizeRepository(context) }
    val progress by repository.progress.collectAsState(initial = MemorizeProgress())
    val scope = rememberCoroutineScope()
    fun save(value: MemorizeProgress) { scope.launch { repository.save(value) } }

    val start = progress.fromPage.toIntOrNull() ?: 0
    val end = progress.toPage.toIntOrNull() ?: 0
    val validPages = start in 1..604 && end in start..604
    val totalPages = if (validPages) end - start + 1 else 1
    val completed = progress.completedPages.coerceIn(0, totalPages)
    val fraction = completed.toFloat() / totalPages

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
                    selected = progress.mode == MemorizeMode.NEW.name,
                    onClick = { if (!progress.started) save(progress.copy(mode = MemorizeMode.NEW.name)) },
                    label = { Text(stringResource(R.string.memorize_mode_new)) }
                )
                FilterChip(
                    selected = progress.mode == MemorizeMode.REVIEW.name,
                    onClick = { if (!progress.started) save(progress.copy(mode = MemorizeMode.REVIEW.name)) },
                    label = { Text(stringResource(R.string.memorize_mode_review)) }
                )
                FilterChip(
                    selected = progress.mode == MemorizeMode.TASMI.name,
                    onClick = { if (!progress.started) save(progress.copy(mode = MemorizeMode.TASMI.name)) },
                    label = { Text(stringResource(R.string.memorize_mode_tasmi)) }
                )
            }

            NoorCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.memorize_plan_title), style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = progress.surah,
                        onValueChange = { if (!progress.started) save(progress.copy(surah = it.take(40))) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.memorize_surah_label)) },
                        placeholder = { Text(stringResource(R.string.memorize_surah_hint)) },
                        singleLine = true,
                        enabled = !progress.started
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = progress.fromPage,
                            onValueChange = { if (!progress.started) save(progress.copy(fromPage = it.filter(Char::isDigit).take(3))) },
                            modifier = Modifier.weight(1f),
                            label = { Text(stringResource(R.string.memorize_from_page)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            enabled = !progress.started
                        )
                        OutlinedTextField(
                            value = progress.toPage,
                            onValueChange = { if (!progress.started) save(progress.copy(toPage = it.filter(Char::isDigit).take(3))) },
                            modifier = Modifier.weight(1f),
                            label = { Text(stringResource(R.string.memorize_to_page)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            enabled = !progress.started
                        )
                    }
                    Text(
                        stringResource(R.string.memorize_pages_count, totalPages),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!progress.started) {
                        Button(
                            onClick = { save(progress.copy(started = true, completedPages = 0)) },
                            enabled = progress.surah.isNotBlank() && validPages,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(stringResource(R.string.memorize_start)) }
                    } else {
                        Text(
                            stringResource(R.string.memorize_session_active, progress.surah),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        LinearProgressIndicator(progress = { fraction }, modifier = Modifier.fillMaxWidth())
                        Text(
                            stringResource(R.string.memorize_progress, completed, totalPages),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = { save(progress.copy(completedPages = (completed + 1).coerceAtMost(totalPages))) },
                            enabled = completed < totalPages,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(stringResource(R.string.memorize_page_done)) }
                        OutlinedButton(
                            onClick = { save(progress.copy(started = false)) },
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
