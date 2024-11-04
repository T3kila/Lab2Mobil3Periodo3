package com.sv.edu.ufg.fis.amb.reproductorapp.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sv.edu.ufg.fis.amb.reproductorapp.models.VideoFile
import com.sv.edu.ufg.fis.amb.reproductorapp.utils.FileUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _videoList = MutableStateFlow<List<VideoFile>>(emptyList())
    val videoList: StateFlow<List<VideoFile>> = _videoList

    fun loadVideos(context: Context) {
        viewModelScope.launch {
            val files = FileUtils.getVideoFiles(context)
            val videos = files.map { file ->
                VideoFile(
                    file = file,
                    name = file.name,
                    uri = FileUtils.getUriForFile(context, file)
                )
            }.sortedByDescending { it.file.lastModified() }
            _videoList.value = videos
        }
    }

    fun deleteVideo(videoFile: VideoFile) {
        if (videoFile.file.exists()) {
            videoFile.file.delete()
            _videoList.value = _videoList.value.filterNot { it.file == videoFile.file }
        }
    }

    fun getLastVideo(): VideoFile? {
        return _videoList.value.maxByOrNull { it.file.lastModified() }
    }

}