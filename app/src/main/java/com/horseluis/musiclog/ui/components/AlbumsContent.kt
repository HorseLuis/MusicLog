package com.horseluis.musiclog.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.horseluis.musiclog.R
import com.horseluis.musiclog.domain.model.AlbumPreference
import com.horseluis.musiclog.domain.model.color
import com.horseluis.musiclog.domain.model.displayText
import com.horseluis.musiclog.domain.model.icon
import com.horseluis.musiclog.ui.screens.SettingsScreen
import com.horseluis.musiclog.ui.state.ModalState
import com.horseluis.musiclog.ui.state.SearchMode
import com.horseluis.musiclog.ui.viewmodel.AlbumsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsContent(
    viewModel: AlbumsViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val searchText by viewModel.searchQuery.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                Spacer(modifier = Modifier.padding(24.dp))
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.local)) },
                    selected = uiState.searchMode == SearchMode.LOCAL,
                    onClick = {
                        viewModel.onSearchModeChanged(SearchMode.LOCAL)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Storage, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.remote)) },
                    selected = uiState.searchMode == SearchMode.REMOTE,
                    onClick = {
                        viewModel.onSearchModeChanged(SearchMode.REMOTE)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Cloud, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.settings)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            val intent = Intent(context, SettingsScreen::class.java)
                            context.startActivity(intent)
                        }
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(
                                if (uiState.searchMode == SearchMode.LOCAL)
                                    R.string.local else R.string.remote
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.menu)
                            )
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.searchMode == SearchMode.LOCAL) {
                        FloatingActionButton(
                            onClick = {
                                val nextPref = AlbumPreference.entries.let {
                                    val currentIndex = it.indexOf(uiState.selectedPreference)
                                    it[(currentIndex + 1) % it.size]
                                }
                                viewModel.onPreferenceFilterChanged(nextPref)
                            },
                            containerColor = uiState.selectedPreference.color(),
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = uiState.selectedPreference.icon(),
                                contentDescription = uiState.selectedPreference.displayText(),
                            )
                        }
                    }

                    FloatingActionButton(
                        onClick = { viewModel.showAddAlbumForm() },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_album),
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
            ) {
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchText,
                            onQueryChange = { viewModel.onSearchChanged(it) },
                            onSearch = { },
                            expanded = false,
                            onExpandedChange = { },
                            placeholder = { Text(stringResource(R.string.search_album)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(R.string.search_icon)
                                )
                            },
                            trailingIcon = {
                                if (searchText.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.onSearchChanged("") }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = stringResource(R.string.clean)
                                        )
                                    }
                                }
                            }
                        )
                    },
                    expanded = false,
                    onExpandedChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) { }
                if (uiState.searchMode == SearchMode.LOCAL) {
                    Text(
                        text = stringResource(R.string.album_count, uiState.albums.size),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.albums.size) { i ->
                        AlbumRowItem(
                            album = uiState.albums[i],
                            onClick = { viewModel.onAlbumSelected(it) },
                            onPreferenceClick = {
                                viewModel.onAlbumSelected(it)
                                viewModel.showPreferenceSelector()
                            }
                        )
                    }
                }
            }
        }
    }

    if (uiState.modalState != ModalState.None) {
        ModalBottomSheet(
            viewModel = viewModel,
            modalState = uiState.modalState,
            selectedAlbum = uiState.selectedAlbum
        )
    }

    if (uiState.isAddAlbumSheetVisible) {
        AddAlbumBottomSheet(
            onDismiss = { viewModel.hideAddAlbumForm() },
            onSave = { name, artist, imageUri, preference ->
                viewModel.addManualAlbum(name, artist, imageUri, preference)
            }
        )
    }
}
