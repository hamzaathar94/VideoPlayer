package com.example.videoplayer.roomDb


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.videoplayer.model.Video

@Dao
interface VideoDao {

    @Insert
    fun insert(video: Video)

    @Query("SELECT * FROM videos")
    fun getAllMusic(): LiveData<List<Video>>

    @Delete
    fun deletemusic(video: Video)

    @Query("SELECT * FROM videos WHERE title LIKE :name ")
    fun search(name: String): LiveData<List<Video>>
}