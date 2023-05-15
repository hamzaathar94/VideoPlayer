package com.example.videoplayer.repository


import android.app.Application
import android.app.RecoverableSecurityException
import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.videoplayer.model.Video
import com.example.videoplayer.roomDb.VideoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class VideoRepository(val context: Context, private val videoDatabase: VideoDatabase) {
    val videoLiveData = MutableLiveData<List<Video>>()

    private var pendingDeleteImage: Video? = null
    private val _permissionNeededForDelete = MutableLiveData<IntentSender?>()
    val permissionNeededForDelete: LiveData<IntentSender?> = _permissionNeededForDelete

    fun getVideos() {
        val videos = mutableListOf<Video>()
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} ASC"
        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            sortOrder
        )
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                val image =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                videos.add(Video(0, title, image))
            } while (cursor.moveToNext())
            cursor.close()
        }
        videoLiveData.postValue(videos)
    }

    fun getVideosLiveData(): LiveData<List<Video>> {
        return videoLiveData
    }


    fun deletePendingImage() {
        pendingDeleteImage?.let { image ->
            pendingDeleteImage = null
            deleteImage(image)
        }
    }
    fun deleteImage(video: Video) {
        GlobalScope.launch {
            performDeleteImage(video)
        }
    }
    private suspend fun performDeleteImage(video: Video) {
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.delete(
                    video.url.toUri(),
                    "${MediaStore.Images.Media._ID} = ?",
                    arrayOf(video.id.toString()
                    )
                )
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException =
                        securityException as? RecoverableSecurityException
                            ?: throw securityException
                    pendingDeleteImage = video
                    _permissionNeededForDelete.postValue(
                        recoverableSecurityException.userAction.actionIntent.intentSender
                    )
                } else {
                    throw securityException
                }
            }
        }
    }
    //room db
    fun getallMusic(): LiveData<List<Video>> {
        return videoDatabase.getMusicDao().getAllMusic()
    }

    suspend fun insertmusic(video: Video) {
        videoDatabase.getMusicDao().insert(video)
    }

    fun deleteMusic(video: Video) {
       // videoDatabase.getMusicDao().deletemusic(video)
    }

    fun search(name: String): LiveData<List<Video>> {
        return videoDatabase.getMusicDao().search(name)
    }

}

