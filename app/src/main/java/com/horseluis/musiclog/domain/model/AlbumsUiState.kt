package com.horseluis.musiclog.domain.model

enum class SearchMode {
    REMOTE, LOCAL
}

data class AlbumsUiState(
    val albums: List<Album> = emptyList(),
    val selectedAlbum: Album? = null,
    val modalState: ModalState = ModalState.None,
    val isAddAlbumSheetVisible: Boolean = false,
    val searchMode: SearchMode = SearchMode.LOCAL,
    val selectedPreference: AlbumPreference = AlbumPreference.TODO
)
