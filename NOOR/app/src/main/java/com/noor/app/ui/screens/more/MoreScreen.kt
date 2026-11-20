package com.noor.app.ui.screens.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noor.app.R
import com.noor.app.domain.ArabicDates
import com.noor.app.ui.components.NoorIcon
import com.noor.app.ui.components.NoorListRow
import com.noor.app.ui.components.ScreenScaffold
import com.noor.app.ui.components.SectionTitle
import com.noor.app.ui.navigation.Routes
import com.noor.app.ui.navigation.Section

@Composable
fun MoreScreen(onNavigate: (String) -> Unit) {
    val moreSections = listOf(
        Section.MEMORIZE, Section.LIBRARY, Section.CALENDAR, Section.DAILY, Section.FAVORITES
    )
    ScreenScaffold(
        title = stringResource(R.string.more_title),
        onBack = null,
        hasBottomBar = true
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionTitle(stringResource(R.string.more_sections_header))
            moreSections.forEach { s ->
                NoorListRow(
                    icon = s.icon,
                    title = stringResource(s.title),
                    trailingChip = ArabicDates.toArabicDigits(stringResource(R.string.phase_label, s.phase)),
                    onClick = { onNavigate(s.route) }
                )
            }
            Spacer(Modifier.height(6.dp))
            SectionTitle(stringResource(R.string.more_app_header))
            NoorListRow(
                icon = NoorIcon.Vector(Icons.Default.Settings),
                title = stringResource(R.string.settings_title),
                onClick = { onNavigate(Routes.SETTINGS) }
            )
            NoorListRow(
                icon = NoorIcon.Vector(Icons.Default.Lock),
                title = stringResource(R.string.privacy_title),
                onClick = { onNavigate(Routes.PRIVACY) }
            )
            NoorListRow(
                icon = NoorIcon.Vector(Icons.Default.Info),
                title = stringResource(R.string.about_title),
                onClick = { onNavigate(Routes.ABOUT) }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
