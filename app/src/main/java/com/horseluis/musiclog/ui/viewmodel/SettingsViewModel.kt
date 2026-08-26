package com.horseluis.musiclog.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.horseluis.musiclog.data.local.AlbumEntity
import com.horseluis.musiclog.data.repository.AlbumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter

class SettingsViewModel(
    application: Application,
    private val repository: AlbumRepository
) : AndroidViewModel(application) {

    fun exportData(uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val albums = repository.getAllLocalAlbums()
                val json = Gson().toJson(albums)

                withContext(Dispatchers.IO) {
                    getApplication<Application>().contentResolver.openOutputStream(uri)?.use { output ->
                        OutputStreamWriter(output).use { writer ->
                            writer.write(json)
                        }
                    }
                }
                onResult(true)
            } catch (_: Exception) {
                onResult(false)
            }
        }
    }

    fun importData(uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val json = withContext(Dispatchers.IO) {
                    getApplication<Application>().contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                }

                if (json != null) {
                    val type = object : TypeToken<List<AlbumEntity>>() {}.type
                    val albums: List<AlbumEntity> = Gson().fromJson(json, type)
                    repository.importAlbums(albums)
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (_: Exception) {
                onResult(false)
            }
        }
    }
}