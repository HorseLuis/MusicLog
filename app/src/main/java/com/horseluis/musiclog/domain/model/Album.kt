package com.horseluis.musiclog.domain.model

data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val imageUrl: String,
    var preference: AlbumPreference? = null
)