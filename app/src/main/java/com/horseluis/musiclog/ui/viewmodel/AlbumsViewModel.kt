package com.horseluis.musiclog.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.horseluis.musiclog.data.repository.AlbumRepository
import com.horseluis.musiclog.domain.model.Album
import com.horseluis.musiclog.domain.model.AlbumPreference
import com.horseluis.musiclog.domain.model.AlbumsUiState
import com.horseluis.musiclog.domain.model.ModalState
import com.horseluis.musiclog.domain.model.SearchMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.core.net.toUri

class AlbumsViewModel(
    application: Application,
    private val repository: AlbumRepository
) : AndroidViewModel(application) {

    val searchQuery = MutableStateFlow("")
    private val _searchMode = MutableStateFlow(SearchMode.LOCAL)
    private val _selectedPreference = MutableStateFlow(AlbumPreference.TODO)

    private val albumsFlow: Flow<List<Album>> =
        repository.searchAlbums(searchQuery, _searchMode, _selectedPreference)

    private val _uiState = MutableStateFlow(AlbumsUiState())

    val uiState: StateFlow<AlbumsUiState> =
        combine(
            albumsFlow,
            _uiState,
            _searchMode,
            _selectedPreference
        ) { albums, uiState, mode, preference ->
            val updatedSelected = uiState.selectedAlbum?.let { selected ->
                albums.find { it.id == selected.id }
            }
            uiState.copy(
                albums = albums,
                selectedAlbum = updatedSelected,
                searchMode = mode,
                selectedPreference = preference
            )
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                AlbumsUiState()
            )

    fun onSearchChanged(query: String) {
        searchQuery.value = query
    }

    fun onSearchModeChanged(mode: SearchMode) {
        _searchMode.value = mode
    }

    fun onPreferenceFilterChanged(preference: AlbumPreference) {
        _selectedPreference.value = preference
    }

    fun onAlbumSelected(album: Album) {
        _uiState.update {
            it.copy(
                selectedAlbum = album,
                modalState = ModalState.Options
            )
        }
    }

    fun showPreferenceSelector() {
        _uiState.update { it.copy(modalState = ModalState.PreferenceSelector) }
    }

    fun closeModal() {
        _uiState.update {
            it.copy(
                modalState = ModalState.None,
                selectedAlbum = null
            )
        }
    }

    fun addToMyAlbums(preference: AlbumPreference) {
        val album = uiState.value.selectedAlbum ?: return

        viewModelScope.launch {
            repository.addToMyAlbums(album, preference)
            searchQuery.value = ""
            closeModal()
        }
    }

    fun removeFromMyAlbums() {
        val album = uiState.value.selectedAlbum ?: return

        viewModelScope.launch {
            repository.removeFromMyAlbums(album)
            searchQuery.value = ""
            closeModal()
        }
    }

    fun showAddAlbumForm() {
        _uiState.update { it.copy(isAddAlbumSheetVisible = true) }
    }

    fun hideAddAlbumForm() {
        _uiState.update { it.copy(isAddAlbumSheetVisible = false) }
    }

    fun addManualAlbum(
        name: String,
        artist: String,
        imageUri: String,
        preference: AlbumPreference
    ) {
        viewModelScope.launch {

            val localPath = saveImageLocally(imageUri)

            val album = Album(
                id = System.currentTimeMillis(),
                name = name,
                artist = artist,
                imageUrl = localPath,
                preference = preference
            )

            repository.addToMyAlbums(album, preference)

            searchQuery.value = ""

            hideAddAlbumForm()
        }
    }

    private suspend fun saveImageLocally(uriString: String): String {
        return withContext(Dispatchers.IO) {

            val context = getApplication<Application>()
            val contentResolver = context.contentResolver

            val inputStream = contentResolver.openInputStream(uriString.toUri())
                ?: throw IllegalStateException("No se pudo abrir la imagen")

            val fileName = "album_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            val outputStream = FileOutputStream(file)

            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            file.absolutePath
        }
    }
}
