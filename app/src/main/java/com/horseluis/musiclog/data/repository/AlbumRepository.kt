package com.horseluis.musiclog.data.repository

import com.horseluis.musiclog.BuildConfig
import com.horseluis.musiclog.data.local.AlbumDao
import com.horseluis.musiclog.data.local.AlbumEntity
import com.horseluis.musiclog.data.network.ApiService
import com.horseluis.musiclog.domain.model.Album
import com.horseluis.musiclog.data.model.AlbumDto
import com.horseluis.musiclog.domain.model.AlbumPreference
import com.horseluis.musiclog.data.model.Image
import com.horseluis.musiclog.ui.state.SearchMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Collections

class AlbumRepository(
    private val apiService: ApiService,
    private val dao: AlbumDao,
) {
    private val defaultCover = "https://dummyimage.com/100.png&text=Album"

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun searchAlbums(
        queryFlow: Flow<String>,
        modeFlow: Flow<SearchMode>,
        preferenceFlow: Flow<AlbumPreference>
    ): Flow<List<Album>> {
        return combine(
            queryFlow.debounce(500).distinctUntilChanged(),
            modeFlow.distinctUntilChanged(),
            preferenceFlow.distinctUntilChanged()
        ) { query, mode, preference ->
            Triple(query, mode, preference)
        }.flatMapLatest { (query, mode, preference) ->
            if (mode == SearchMode.LOCAL) {
                if (AlbumPreference.TODO == preference) {
                    dao.observeFiltered(query)
                        .map { entities ->
                            entities.map { it.toDomain() }
                        }
                } else {
                    dao.observeFiltered(query, preference.name)
                        .map { entities ->
                            entities.map { it.toDomain() }
                        }
                }
            } else {
                if (query.isBlank()) {
                    dao.observeAll()
                        .map { entities ->
                            entities.sortedByDescending { it.savedDate }
                                .map { it.toDomain() }
                        }
                } else {
                    flow {
                        val apiResults = searchAlbums(query)
                        emit(apiResults)
                    }.combine(dao.observeAll()) { apiResults, savedAlbums ->
                        val savedMap = savedAlbums.associateBy { it.id }
                        apiResults.map { dto ->
                            val saved = savedMap[dto.mbid]
                            Album(
                                id = dto.mbid,
                                name = dto.name,
                                artist = dto.artist,
                                imageUrl = getImageUrl(dto.image)
                                    ?: defaultCover,
                                preference = saved?.let {
                                    AlbumPreference.valueOf(it.preference)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getImageUrl(images: List<Image>): String? {
        val sizeOrder = listOf("large", "medium", "small")
        return sizeOrder
            .mapNotNull { size ->
                images.find { it.size == size }?.text
            }
            .firstOrNull {
                it.isNotBlank()
            }?.ifBlank { null }
    }

    private suspend fun searchAlbums(query: String): List<AlbumDto> {
        val apiKey = BuildConfig.API_KEY
        val response = apiService.searchAlbums(
            album = query,
            apiKey = apiKey
        )
        return if (response.isSuccessful) {
            response.body()?.results?.albummatches?.album
                ?: Collections.emptyList()
        } else {
            Collections.emptyList()
        }
    }

    private fun AlbumEntity.toDomain(): Album {
        return Album(
            id = this.id,
            name = this.name,
            artist = this.artist,
            imageUrl = this.imageUrl,
            preference = AlbumPreference.valueOf(this.preference)
        )
    }

    suspend fun addToMyAlbums(album: Album, preference: AlbumPreference) {
        dao.insert(
            AlbumEntity(
                id = album.id,
                name = album.name,
                artist = album.artist,
                imageUrl = album.imageUrl,
                preference = preference.name,
                savedDate = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeFromMyAlbums(album: Album) {
        dao.delete(album.id)
    }
}
