package com.noor.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noor.app.R
import com.noor.app.data.settings.AppSettings
import com.noor.app.data.settings.ThemeMode
import com.noor.app.ui.components.NoorCard
import com.noor.app.ui.components.ScreenScaffold
import com.noor.app.ui.components.SectionTitle
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val savedMsg = stringResource(R.string.settings_name_saved)
    val deletedMsg = stringResource(R.string.settings_deleted)

    var nameField by rememberSaveable(settings.userName) { mutableStateOf(settings.userName) }
    var sliderValue by remember(settings.fontScale) { mutableFloatStateOf(settings.fontScale) }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }

    ScreenScaffold(
        title = stringResource(R.string.settings_title),
        onBack = onBack,
        snackbarHost = snackbar
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // المظهر
            SectionTitle(stringResource(R.string.settings_appearance))
            NoorCard(Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 6.dp, horizontal = 8.dp)) {
                Column {
                    ThemeOption(R.string.theme_system, ThemeMode.SYSTEM, settings.themeMode) { viewModel.setThemeMode(it) }
                    ThemeOption(R.string.theme_light, ThemeMode.LIGHT, settings.themeMode) { viewModel.setThemeMode(it) }
                    ThemeOption(R.string.theme_dark, ThemeMode.DARK, settings.themeMode) { viewModel.setThemeMode(it) }
                }
            }

            // حجم الخط
            SectionTitle(stringResource(R.string.settings_font_size))
            NoorCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        stringResource(R.string.settings_font_preview),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        onValueChangeFinished = { viewModel.setFontScale(sliderValue) },
                        valueRange = AppSettings.MIN_FONT_SCALE..AppSettings.MAX_FONT_SCALE
                    )
                    TextButton(
                        onClick = {
                            sliderValue = AppSettings.DEFAULT_FONT_SCALE
                            viewModel.setFontScale(AppSettings.DEFAULT_FONT_SCALE)
                        }
                    ) { Text(stringResource(R.string.reset)) }
                }
            }

            // الملف الشخصي
            SectionTitle(stringResource(R.string.settings_profile))
            NoorCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameField,
                        onValueChange = { nameField = it.take(AppSettings.MAX_NAME_LENGTH) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.settings_name_label)) },
                        supportingText = { Text(stringResource(R.string.settings_name_hint)) },
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            viewModel.setUserName(nameField)
                            scope.launch { snackbar.showSnackbar(savedMsg) }
                        }
                    ) { Text(stringResource(R.string.save)) }
                }
            }

            // البيانات
            SectionTitle(stringResource(R.string.settings_data))
            NoorCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        stringResource(R.string.settings_data_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(onClick = { confirmDelete = true }) {
                        Text(stringResource(R.string.settings_delete_all), color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Text(
                stringResource(R.string.settings_more_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.settings_delete_title)) },
            text = { Text(stringResource(R.string.settings_delete_body)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    viewModel.clearAll()
                    scope.launch { snackbar.showSnackbar(deletedMsg) }
                }) { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
private fun ThemeOption(
    label: Int,
    mode: ThemeMode,
    current: ThemeMode,
    onSelect: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = current == mode, role = Role.RadioButton, onClick = { onSelect(mode) })
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = current == mode, onClick = null)
        Text(
            stringResource(label),
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
