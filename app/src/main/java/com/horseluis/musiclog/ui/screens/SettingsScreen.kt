package com.horseluis.musiclog.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.horseluis.musiclog.data.local.AppDatabase
import com.horseluis.musiclog.data.network.RetrofitClient
import com.horseluis.musiclog.data.repository.AlbumRepository
import com.horseluis.musiclog.R
import com.horseluis.musiclog.ui.theme.MusicLogTheme
import com.horseluis.musiclog.ui.viewmodel.SettingsViewModel

class SettingsScreen : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(applicationContext)
        val repository = AlbumRepository(RetrofitClient.apiService, db.albumDao())
        val viewModel = SettingsViewModel(application, repository)

        setContent {
            MusicLogTheme {
                val exportLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.CreateDocument("application/json")
                ) { uri ->
                    uri?.let { viewModel.exportData(it) { success ->
                        val message = if (success) getString(R.string.export_ok) else getString(R.string.export_error)
                        Toast.makeText(this@SettingsScreen, message, Toast.LENGTH_SHORT).show()
                    }}
                }

                val importLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.OpenDocument()
                ) { uri ->
                    uri?.let { viewModel.importData(it) { success ->
                        val message = if (success) getString(R.string.import_ok) else getString(R.string.import_error)
                        Toast.makeText(this@SettingsScreen, message, Toast.LENGTH_SHORT).show()
                    }}
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.settings)) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                                }
                            }
                        )
                    }
                ) { padding ->
                    Column(modifier = Modifier.padding(padding)) {
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.import_title)) },
                            supportingContent = { Text(stringResource(R.string.import_content)) },
                            leadingContent = { 
                                Icon(Icons.Default.FileDownload, contentDescription = null) 
                            },
                            modifier = Modifier.clickable { 
                                importLauncher.launch(arrayOf("application/json")) 
                            }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text(stringResource(R.string.export_title)) },
                            supportingContent = { Text(stringResource(R.string.export_content)) },
                            leadingContent = { 
                                Icon(Icons.Default.FileUpload, contentDescription = null) 
                            },
                            modifier = Modifier.clickable { 
                                exportLauncher.launch("musiclog_backup.json") 
                            }
                        )
                    }
                }
            }
        }
    }
}
