package com.horseluis.musiclog.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.horseluis.musiclog.R

enum class AlbumPreference(
    val textRes: Int
) {
    TODO(R.string.preference_all),
    POR_DECIDIR(R.string.preference_pending),
    ME_GUSTA(R.string.preference_like),
    NO_ME_GUSTA(R.string.preference_dislike),
    INDIFERENTE(R.string.preference_indif)

}

@Composable
fun AlbumPreference.displayText(): String {
    return stringResource(id = textRes)
}

fun AlbumPreference.icon() = when (this) {
    AlbumPreference.TODO -> Icons.Default.FilterList
    AlbumPreference.POR_DECIDIR -> Icons.Default.Schedule
    AlbumPreference.ME_GUSTA -> Icons.Default.Favorite
    AlbumPreference.NO_ME_GUSTA -> Icons.Default.Close
    AlbumPreference.INDIFERENTE -> Icons.Default.Remove
}

fun AlbumPreference.color(): Color = when (this) {
    AlbumPreference.TODO -> Color(0xFF2196F3)
    AlbumPreference.POR_DECIDIR -> Color(0xFF9E9E9E)
    AlbumPreference.ME_GUSTA -> Color(0xFF4CAF50)
    AlbumPreference.NO_ME_GUSTA -> Color(0xFFF44336)
    AlbumPreference.INDIFERENTE -> Color(0xFFFF9800)
}