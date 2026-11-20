package com.noor.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.noor.app.R
import com.noor.app.ui.components.NoorIcon
import com.noor.app.ui.components.NoorIconView
import com.noor.app.ui.screens.about.AboutScreen
import com.noor.app.ui.screens.calendar.CalendarScreen
import com.noor.app.ui.screens.home.HomeScreen
import com.noor.app.ui.screens.more.MoreScreen
import com.noor.app.ui.screens.memorize.MemorizeScreen
import com.noor.app.ui.screens.prayer.PrayerScreen
import com.noor.app.ui.screens.privacy.PrivacyScreen
import com.noor.app.ui.screens.quran.QuranScreen
import com.noor.app.ui.screens.section.SectionScreen
import com.noor.app.ui.screens.settings.SettingsScreen
import com.noor.app.ui.screens.settings.SettingsViewModel
import com.noor.app.ui.screens.splash.SplashScreen

private data class TabItem(val route: String, val label: Int, val icon: NoorIcon)

private val tabs = listOf(
    TabItem(Routes.HOME, R.string.nav_home, NoorIcon.Vector(Icons.Default.Home)),
    TabItem(Routes.QURAN, R.string.nav_quran, NoorIcon.Res(R.drawable.ic_book)),
    TabItem(Routes.PRAYER, R.string.nav_prayer, NoorIcon.Res(R.drawable.ic_mosque)),
    TabItem(Routes.AZKAR, R.string.nav_azkar, NoorIcon.Res(R.drawable.ic_beads)),
    TabItem(Routes.MORE, R.string.nav_more, NoorIcon.Vector(Icons.Default.Menu))
)

@Composable
fun NoorApp(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBar = currentRoute in Routes.topLevel
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AnimatedVisibility(
                visible = showBar,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(120))
            ) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = { navController.navigateTab(tab.route) },
                            icon = { NoorIconView(tab.icon, tint = LocalContentColor.current) },
                            label = { Text(stringResource(tab.label)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { inner ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(PaddingValues(bottom = inner.calculateBottomPadding())),
            enterTransition = { fadeIn(tween(260)) + slideInVertically(tween(260)) { it / 24 } },
            exitTransition = { fadeOut(tween(160)) },
            popEnterTransition = { fadeIn(tween(220)) },
            popExitTransition = { fadeOut(tween(160)) + androidx.compose.animation.slideOutVertically(tween(220)) { it / 24 } }
        ) {
            composable(
                Routes.SPLASH,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                SplashScreen(onFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                })
            }
            composable(Routes.HOME) {
                HomeScreen(
                    userName = settings.userName,
                    onOpenSection = { s -> navController.navigateFromHome(s.route) }
                )
            }
            // أقسام الشريط السفلي + الأقسام الفرعية: شاشة حالة حقيقية إلى أن تُنفَّذ.
            Section.entries.forEach { section ->
                composable(section.route) {
                    if (section == Section.MEMORIZE) {
                        MemorizeScreen(onBack = { navController.popBackStack() })
                    } else if (section == Section.QURAN) {
                        QuranScreen()
                    } else if (section == Section.PRAYER) {
                        PrayerScreen(onBack = { navController.popBackStack() })
                    } else if (section == Section.CALENDAR) {
                        CalendarScreen(onBack = { navController.popBackStack() })
                    } else {
                        val top = section.route in Routes.topLevel
                        SectionScreen(
                            section = section,
                            hasBottomBar = top,
                            onBack = if (top) null else ({ navController.popBackStack() })
                        )
                    }
                }
            }
            composable(Routes.MORE) {
                MoreScreen(onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } })
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(settingsViewModel, onBack = { navController.popBackStack() })
            }
            composable(Routes.PRIVACY) { PrivacyScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.ABOUT) { AboutScreen(onBack = { navController.popBackStack() }) }
        }
    }
}

private fun NavHostController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** من الرئيسية: الأقسام ذات التبويب تُفتح كتبويب، والبقية كشاشة فرعية. */
private fun NavHostController.navigateFromHome(route: String) {
    if (route in Routes.topLevel) navigateTab(route) else navigate(route) { launchSingleTop = true }
}
