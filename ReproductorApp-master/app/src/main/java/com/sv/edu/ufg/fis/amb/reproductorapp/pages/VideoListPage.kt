package com.sv.edu.ufg.fis.amb.reproductorapp.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sv.edu.ufg.fis.amb.reproductorapp.models.VideoFile
import com.sv.edu.ufg.fis.amb.reproductorapp.viewModels.MainViewModel

@Composable
fun VideoListPage(
    onVideoSelected: (VideoFile) -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadVideos(context)
    }

    val videos by viewModel.videoList.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        items(videos) { video ->
            VideoListItem(
                videoFile = video,
                onClick = { onVideoSelected(video) },
                onDelete = { viewModel.deleteVideo(video) }
            )
            Divider()
        }
    }
}

@Composable
fun VideoListItem(
    videoFile: VideoFile,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = videoFile.name, modifier = Modifier.weight(1f))
        IconButton(onClick = onDelete) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar")
        }
    }
}