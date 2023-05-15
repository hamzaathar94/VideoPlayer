package com.example.videoplayer.viewModel

import android.annotation.SuppressLint
import android.app.RecoverableSecurityException
import android.content.Context
import android.content.IntentSender
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoplayer.model.Video
import com.example.videoplayer.repository.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoViewModel(private val videoRepository: VideoRepository) : ViewModel() {

    val video: MutableLiveData<List<Video>> = videoRepository.videoLiveData
    val videordb: LiveData<List<Video>> = videoRepository.getallMusic()
    @SuppressLint("StaticFieldLeak")
    val context: Context? = null


    fun getVideos() {
        videoRepository.getVideos()
    }

    fun getVideoLiveData(): LiveData<List<Video>> {
        videoRepository.getVideosLiveData()
        return video
    }


    //roomdb
    fun insertMusic(title: String, url: String) {
        viewModelScope.launch {
            val video = Video(null, title, url)
            videoRepository.insertmusic(video)
        }
    }

    fun deleteMusic(video: Video) {
        viewModelScope.launch {
//            videoRepository.deleteMusic(video)
        }
    }

    fun search(title: String): LiveData<List<Video>> {
        return videoRepository.search(title)
    }
    //

    private var pendingDeleteVideo: Video? = null
    private val _permissionNeededForDelete = MutableLiveData<IntentSender?>()
    val permissionNeededForDelete: LiveData<IntentSender?> = _permissionNeededForDelete

    //delete permission needed
    fun deletePendingVideo() {
        pendingDeleteVideo?.let { video ->
            pendingDeleteVideo = null
            deleteVideo(video)
        }
    }

    private fun deleteVideo(video: Video) {
        viewModelScope.launch {
            performDeleteVideo(video)
        }
    }

    private suspend fun performDeleteVideo(video: Video) {
        withContext(Dispatchers.IO) {
            try {
                context?.contentResolver?.delete(
                    video.url.toUri(),
                    "${MediaStore.Video.Media._ID} = ?",
                    arrayOf(video.id.toString())
                )
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException =
                        securityException as? RecoverableSecurityException
                            ?: throw securityException
                    pendingDeleteVideo = video
                    _permissionNeededForDelete.postValue(
                        recoverableSecurityException.userAction.actionIntent.intentSender
                    )
                } else {
                    throw securityException
                }
            }
        }
    }
}