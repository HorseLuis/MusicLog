package com.horseluis.musiclog.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_albums")
data class AlbumEntity(
    @PrimaryKey val id: String,
    val name: String,
    val artist: String,
    val imageUrl: String,
    val preference: String,
    val savedDate: Long
)