package com.horseluis.musiclog.data.repository

import com.horseluis.musiclog.domain.model.AlbumResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AlbumApiService {
    @GET("search?entity=album")
    suspend fun searchAlbums(
        @Query("term") term: String
    ): AlbumResponse
}