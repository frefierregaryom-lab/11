package com.noor.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.noor.app.R
import com.noor.app.domain.ArabicDates
import com.noor.app.ui.components.IconBadge
import com.noor.app.ui.components.IslamicPattern
import com.noor.app.ui.components.NoorCard
import com.noor.app.ui.components.NoorStarMark
import com.noor.app.ui.components.SectionTitle
import com.noor.app.ui.components.StatusChip
import com.noor.app.ui.navigation.Section
import com.noor.app.ui.theme.Emerald700
import com.noor.app.ui.theme.Emerald800
import com.noor.app.ui.theme.Emerald900
import com.noor.app.ui.theme.Gold300
import com.noor.app.ui.theme.Gold400
import com.noor.app.ui.theme.Gold500
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun HomeScreen(
    userName: String,
    onOpenSection: (Section) -> Unit
) {
    val now = remember { LocalDateTime.now() }
    val today: LocalDate = now.toLocalDate()

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HomeHero(userName = userName, today = today, hour = now.hour)

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PendingCard(
                icon = Section.PRAYER,
                title = stringResource(R.string.home_next_prayer),
                body = stringResource(R.string.home_next_prayer_body),
                onClick = { onOpenSection(Section.PRAYER) }
            )

            PrayerTimesPreview(onClick = { onOpenSection(Section.PRAYER) })

            PendingCard(
                icon = Section.QURAN,
                title = stringResource(R.string.home_verse_title),
                body = stringResource(R.string.home_verse_body),
                onClick = { onOpenSection(Section.QURAN) }
            )
            PendingCard(
                icon = Section.AZKAR,
                title = stringResource(R.string.home_dhikr_title),
                body = stringResource(R.string.home_dhikr_body),
                onClick = { onOpenSection(Section.AZKAR) }
            )
            PendingCard(
                icon = Section.DAILY,
                title = stringResource(R.string.home_progress_title),
                body = stringResource(R.string.home_progress_body),
                onClick = { onOpenSection(Section.DAILY) }
            )
            PendingCard(
                icon = Section.MEMORIZE,
                title = stringResource(R.string.home_continue_title),
                body = stringResource(R.string.home_continue_body),
                onClick = { onOpenSection(Section.MEMORIZE) }
            )
            PendingCard(
                icon = Section.FAVORITES,
                title = stringResource(R.string.home_achievements_title),
                body = stringResource(R.string.home_achievements_body),
                onClick = { onOpenSection(Section.DAILY) }
            )

            SectionTitle(stringResource(R.string.home_sections_title))
            val sections = Section.entries
            sections.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    pair.forEach { s ->
                        SectionTile(s, Modifier.weight(1f)) { onOpenSection(s) }
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun HomeHero(userName: String, today: LocalDate, hour: Int) {
    val greeting = stringResource(
        if (ArabicDates.dayPart(hour) == ArabicDates.DayPart.MORNING) R.string.greeting_morning
        else R.string.greeting_evening
    )
    val greetingLine = if (userName.isBlank()) greeting
    else stringResource(R.string.greeting_with_name, greeting, userName)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
            .background(Brush.linearGradient(listOf(Emerald700, Emerald800, Emerald900)))
    ) {
        IslamicPattern(
            modifier = Modifier.matchParentSize(),
            color = Gold400.copy(alpha = 0.16f)
        )
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 22.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NoorStarMark(
                    modifier = Modifier.size(34.dp),
                    color = Gold500,
                    holeColor = Emerald900,
                    coreColor = Gold300
                )
                Text(
                    stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Gold300
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.home_salam),
                style = MaterialTheme.typography.labelLarge,
                color = Gold300.copy(alpha = 0.9f)
            )
            Text(
                text = greetingLine,
                style = MaterialTheme.typography.headlineMedium,
                color = androidx.compose.ui.graphics.Color.White
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = ArabicDates.weekdayName(today) + "، " +
                    ArabicDates.hijri(today, stringResource(R.string.hijri_suffix)),
                style = MaterialTheme.typography.titleLarge,
                color = Gold300
            )
            Text(
                text = ArabicDates.gregorian(today),
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = Gold300.copy(alpha = 0.85f)
            )
        }
    }
}

/** بطاقة لميزة لم تُفعَّل بعد: عنوانها الحقيقي مع شرح صريح لحالتها. */
@Composable
private fun PendingCard(icon: Section, title: String, body: String, onClick: () -> Unit) {
    NoorCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.Top) {
            IconBadge(icon.icon)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    StatusChip(ArabicDates.toArabicDigits(stringResource(R.string.phase_label, icon.phase)))
                }
                Text(
                    body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PrayerTimesPreview(onClick: () -> Unit) {
    val names = listOf(
        R.string.prayer_fajr, R.string.prayer_dhuhr, R.string.prayer_asr,
        R.string.prayer_maghrib, R.string.prayer_isha
    )
    NoorCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.home_prayer_times), style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                names.forEach { n ->
                    Text(
                        stringResource(n),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                stringResource(R.string.home_prayer_times_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionTile(section: Section, modifier: Modifier, onClick: () -> Unit) {
    NoorCard(modifier = modifier, onClick = onClick, contentPadding = PaddingValues(14.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconBadge(section.icon, size = 48.dp)
            Text(
                stringResource(section.title),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}
