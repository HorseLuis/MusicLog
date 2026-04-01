package com.horseluis.musiclog.data.network

import com.horseluis.musiclog.data.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("2.0/")
    suspend fun searchAlbums(
        @Query("method") method: String = "album.search",
        @Query("album") album: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: String = "10"
    ): Response<SearchResponse>
}