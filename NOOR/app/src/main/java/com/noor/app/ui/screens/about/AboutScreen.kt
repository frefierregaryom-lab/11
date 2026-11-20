package com.noor.app.ui.screens.about

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noor.app.R
import com.noor.app.domain.ArabicDates
import com.noor.app.ui.components.NoorCard
import com.noor.app.ui.components.NoorStarMark
import com.noor.app.ui.components.ScreenScaffold
import com.noor.app.ui.theme.Emerald900
import com.noor.app.ui.theme.Gold300
import com.noor.app.ui.theme.Gold500

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val version = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }
    ScreenScaffold(title = stringResource(R.string.about_title), onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            NoorStarMark(
                modifier = Modifier.size(88.dp),
                color = Gold500,
                holeColor = MaterialTheme.colorScheme.background,
                coreColor = Gold300
            )
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
            Text(stringResource(R.string.app_tagline), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
            Text(
                ArabicDates.toArabicDigits(stringResource(R.string.about_version, version)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            InfoCard(R.string.about_status_title, R.string.about_status_body)
            InfoCard(R.string.about_sources_title, R.string.about_sources_body)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoCard(title: Int, body: Int) {
    NoorCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(stringResource(title), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text(stringResource(body), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
