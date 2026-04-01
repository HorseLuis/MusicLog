package com.horseluis.musiclog.data.model


data class AlbumDto(
    val mbid: String,
    val name: String,
    val artist: String,
    val image: List<Image>,
)