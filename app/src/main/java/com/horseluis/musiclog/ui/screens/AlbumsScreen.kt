package com.horseluis.musiclog.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.horseluis.musiclog.R
import com.horseluis.musiclog.data.local.AppDatabase
import com.horseluis.musiclog.data.network.RetrofitClient
import com.horseluis.musiclog.data.repository.AlbumRepository
import com.horseluis.musiclog.ui.components.AlbumsContent
import com.horseluis.musiclog.ui.state.SearchMode
import com.horseluis.musiclog.ui.theme.MusicLogTheme
import com.horseluis.musiclog.ui.viewmodel.AlbumsViewModel

class AlbumsScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modeString = intent.getStringExtra("SEARCH_MODE") ?: getString(R.string.local)
        val initialMode = SearchMode.valueOf(modeString)

        val db = AppDatabase.getDatabase(this)

        val repository = AlbumRepository(
            RetrofitClient.apiService,
            db.albumDao()
        )

        val viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AlbumsViewModel(
                        application,
                        repository
                    ) as T
                }
            }
        )[AlbumsViewModel::class.java]
        viewModel.onSearchModeChanged(initialMode)

        setContent {
            MusicLogTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AlbumsContent(viewModel)
                }
            }
        }
    }
}
