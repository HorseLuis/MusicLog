package com.horseluis.musiclog.data.model


data class SearchResponse(
    val results: Results
)

data class Results(
    val albummatches: AlbumMatches
)

data class AlbumMatches(
    val album: List<AlbumDto>
)