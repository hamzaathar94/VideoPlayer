package com.example.videoplayer.roomDb


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.videoplayer.model.Video


@Database(entities = [(Video::class)], version = 1, exportSchema = false)
abstract class VideoDatabase : RoomDatabase() {
    abstract fun getMusicDao(): VideoDao

    companion object {
        private var INSTANCE: VideoDatabase? = null

        fun getDataBase(context: Context): VideoDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        VideoDatabase::class.java,
                        "Video_database"
                    ).allowMainThreadQueries().build()
                }
            }
            return INSTANCE!!
        }
    }
}