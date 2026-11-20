package com.noor.app.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noor.app.R
import com.noor.app.ui.components.IslamicPattern
import com.noor.app.ui.components.NoorStarMark
import com.noor.app.ui.theme.Emerald700
import com.noor.app.ui.theme.Emerald900
import com.noor.app.ui.theme.Emerald950
import com.noor.app.ui.theme.Gold300
import com.noor.app.ui.theme.Gold400
import com.noor.app.ui.theme.Gold500
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val progress = remember { Animatable(0f) }
    val finished by rememberUpdatedState(onFinished)

    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(durationMillis = 1300, easing = FastOutSlowInEasing))
        delay(500)
        finished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Emerald700, Emerald900, Emerald950))),
        contentAlignment = Alignment.Center
    ) {
        IslamicPattern(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.16f * progress.value),
            color = Gold400
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            NoorStarMark(
                modifier = Modifier
                    .size(124.dp)
                    .scale(0.6f + 0.4f * progress.value)
                    .rotate(45f * (1f - progress.value))
                    .alpha(progress.value),
                color = Gold500,
                holeColor = Emerald900,
                coreColor = Gold300
            )
            Text(
                text = stringResource(R.string.app_name),
                modifier = Modifier.alpha(progress.value),
                style = MaterialTheme.typography.displayLarge,
                color = Gold300
            )
            Text(
                text = stringResource(R.string.app_tagline),
                modifier = Modifier.alpha(progress.value),
                style = MaterialTheme.typography.titleMedium,
                color = Gold300.copy(alpha = 0.85f)
            )
        }
    }
}
