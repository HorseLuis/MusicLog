package com.horseluis.musiclog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.horseluis.musiclog.R
import com.horseluis.musiclog.domain.model.Album
import com.horseluis.musiclog.domain.model.AlbumPreference
import com.horseluis.musiclog.domain.model.ModalState
import com.horseluis.musiclog.ui.viewmodel.AlbumsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheet(
    viewModel: AlbumsViewModel,
    modalState: ModalState,
    selectedAlbum: Album?
) {
    ModalBottomSheet(
        onDismissRequest = { viewModel.closeModal() }
    ) {
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        val removedAlbum = stringResource(R.string.removed_album)
        val addedLike = stringResource(R.string.added_like)
        val addedDislike = stringResource(R.string.added_dislike)
        val addedIndif = stringResource(R.string.added_indif)
        val addedToPending = stringResource(R.string.added_to_pending)

        when (modalState) {

            ModalState.Options -> {
                if (null == selectedAlbum?.preference) {
                    OptionItem(stringResource(R.string.add_to_albums)) {
                        viewModel.showPreferenceSelector()
                    }
                } else {
                    OptionItem(stringResource(R.string.remove_from_albums)) {
                        viewModel.removeFromMyAlbums()
                        scope.launch {
                            snackbarHostState.showSnackbar(removedAlbum)
                        }
                    }
                }
            }

            ModalState.PreferenceSelector -> {
                PreferenceOption(stringResource(R.string.preference_like)) {
                    viewModel.addToMyAlbums(AlbumPreference.ME_GUSTA)
                    scope.launch {
                        snackbarHostState.showSnackbar(addedLike)
                    }
                }

                PreferenceOption(stringResource(R.string.preference_dislike)) {
                    viewModel.addToMyAlbums(AlbumPreference.NO_ME_GUSTA)
                    scope.launch {
                        snackbarHostState.showSnackbar(addedDislike)
                    }
                }

                PreferenceOption(stringResource(R.string.preference_indif)) {
                    viewModel.addToMyAlbums(AlbumPreference.INDIFERENTE)
                    scope.launch {
                        snackbarHostState.showSnackbar(addedIndif)
                    }
                }

                PreferenceOption(stringResource(R.string.preference_pending)) {
                    viewModel.addToMyAlbums(AlbumPreference.POR_DECIDIR)
                    scope.launch {
                        snackbarHostState.showSnackbar(addedToPending)
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
fun OptionItem(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    )
}

@Composable
fun PreferenceOption(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    )
}