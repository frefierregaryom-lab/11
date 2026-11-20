package com.noor.app.ui.screens.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noor.app.R
import com.noor.app.domain.ArabicDates
import com.noor.app.ui.components.IconBadge
import com.noor.app.ui.components.NoorCard
import com.noor.app.ui.components.ScreenScaffold
import com.noor.app.ui.components.SectionTitle
import com.noor.app.ui.components.StatusChip
import com.noor.app.ui.navigation.Section

/**
 * شاشة قسم لم يُنفَّذ بعد. تعرض حالته الحقيقية وما سيتضمنه، دون أزرار أو بيانات شكلية.
 * تُستبدل بالشاشة الفعلية عند الوصول إلى مرحلة القسم.
 */
@Composable
fun SectionScreen(
    section: Section,
    hasBottomBar: Boolean,
    onBack: (() -> Unit)?
) {
    ScreenScaffold(
        title = stringResource(section.title),
        onBack = onBack,
        hasBottomBar = hasBottomBar
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconBadge(section.icon, size = 72.dp)
                Text(
                    text = stringResource(section.summary),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            NoorCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            stringResource(R.string.section_pending_title),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        StatusChip(stringResource(R.string.status_in_development))
                    }
                    Text(
                        text = ArabicDates.toArabicDigits(stringResource(R.string.section_pending_body, section.phase)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            SectionTitle(stringResource(R.string.section_features_title))
            NoorCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    stringArrayResource(section.features).forEach { feature ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                "◆",
                                modifier = Modifier.padding(top = 4.dp, end = 10.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(feature, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
