package com.noor.app.ui.navigation

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import com.noor.app.R
import com.noor.app.ui.components.NoorIcon

/**
 * أقسام التطبيق الرئيسية وحالتها الحقيقية.
 * [phase] هو رقم مرحلة التنفيذ (حسب خطة المشروع) التي يُفعَّل فيها القسم.
 * لا يُعرض أي قسم كأنه يعمل قبل تنفيذه.
 */
enum class Section(
    val route: String,
    @StringRes val title: Int,
    @StringRes val summary: Int,
    @ArrayRes val features: Int,
    val phase: Int,
    val icon: NoorIcon
) {
    QURAN(Routes.QURAN, R.string.section_quran, R.string.summary_quran, R.array.features_quran, 4, NoorIcon.Res(R.drawable.ic_book)),
    MEMORIZE(Routes.MEMORIZE, R.string.section_memorize, R.string.summary_memorize, R.array.features_memorize, 6, NoorIcon.Res(R.drawable.ic_memorize)),
    PRAYER(Routes.PRAYER, R.string.section_prayer, R.string.summary_prayer, R.array.features_prayer, 5, NoorIcon.Res(R.drawable.ic_mosque)),
    AZKAR(Routes.AZKAR, R.string.section_azkar, R.string.summary_azkar, R.array.features_azkar, 4, NoorIcon.Res(R.drawable.ic_beads)),
    LIBRARY(Routes.LIBRARY, R.string.section_library, R.string.summary_library, R.array.features_library, 7, NoorIcon.Res(R.drawable.ic_library)),
    CALENDAR(Routes.CALENDAR, R.string.section_calendar, R.string.summary_calendar, R.array.features_calendar, 8, NoorIcon.Vector(Icons.Default.DateRange)),
    DAILY(Routes.DAILY, R.string.section_daily, R.string.summary_daily, R.array.features_daily, 8, NoorIcon.Vector(Icons.Default.CheckCircle)),
    FAVORITES(Routes.FAVORITES, R.string.section_favorites, R.string.summary_favorites, R.array.features_favorites, 9, NoorIcon.Vector(Icons.Default.Favorite));
}
