package com.noor.app.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val QURAN = "quran"
    const val PRAYER = "prayer"
    const val AZKAR = "azkar"
    const val MORE = "more"
    const val MEMORIZE = "memorize"
    const val LIBRARY = "library"
    const val CALENDAR = "calendar"
    const val DAILY = "daily"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val PRIVACY = "privacy"
    const val ABOUT = "about"

    /** المسارات التي يظهر فيها الشريط السفلي. */
    val topLevel = setOf(HOME, QURAN, PRAYER, AZKAR, MORE)
}
