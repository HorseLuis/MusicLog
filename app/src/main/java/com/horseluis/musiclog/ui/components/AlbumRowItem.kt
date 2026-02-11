package com.horseluis.musiclog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.horseluis.musiclog.domain.model.Album
import com.horseluis.musiclog.domain.model.color
import com.horseluis.musiclog.domain.model.displayText
import com.horseluis.musiclog.domain.model.icon

@Composable
fun AlbumRowItem(
    album: Album,
    onClick: (Album) -> Unit,
    onPreferenceClick: (Album) -> Unit
) {
    val preference = album.preference
    val indicatorColor = preference?.color() ?: Color.Transparent

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(album) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(indicatorColor)
            )

            ListItem(
                headlineContent = {
                    Text(
                        text = album.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                supportingContent = {
                    Column {
                        Text(
                            text = album.artist,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (preference != null) {
                            AssistChip(
                                onClick = { onPreferenceClick(album) },
                                label = { Text(preference.displayText()) },
                                leadingIcon = {
                                    Icon(
                                        preference.icon(),
                                        contentDescription = null,
                                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    labelColor = preference.color(),
                                    leadingIconContentColor = preference.color()
                                ),
                                border = AssistChipDefaults.assistChipBorder(
                                    enabled = true
                                )
                            )
                        }
                    }
                },
                leadingContent = {
                    AsyncImage(
                        model = album.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }
    }
}