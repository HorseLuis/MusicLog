package com.horseluis.musiclog.data.repository

import com.horseluis.musiclog.data.local.AlbumDao
import com.horseluis.musiclog.data.local.AlbumEntity
import com.horseluis.musiclog.domain.model.Album
import com.horseluis.musiclog.domain.model.AlbumPreference
import com.horseluis.musiclog.domain.model.SearchMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AlbumRepository(
    private val api: AlbumApiService,
    private val dao: AlbumDao
) {

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
                        val apiResults = api.searchAlbums(query).results
                        emit(apiResults)
                    }.combine(dao.observeAll()) { apiResults, savedAlbums ->
                        val savedMap = savedAlbums.associateBy { it.id }
                        apiResults.map { dto ->
                            val saved = savedMap[dto.collectionId]
                            Album(
                                id = dto.collectionId,
                                name = dto.collectionName,
                                artist = dto.artistName,
                                imageUrl = dto.artworkUrl100,
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
