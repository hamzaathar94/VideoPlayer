package com.example.videoplayer.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.videoplayer.repository.VideoRepository
import com.example.videoplayer.viewModel.VideoViewModel


class MediaFectory(val fileRepository: VideoRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VideoViewModel(fileRepository) as T
    }
}