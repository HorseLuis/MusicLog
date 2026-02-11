package com.horseluis.musiclog.domain.model


data class AlbumDto(
    val collectionId: Long,
    val collectionName: String,
    val artistName: String,
    val artworkUrl100: String
)