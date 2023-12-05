package com.klevcsoo.snapreealandroid

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.klevcsoo.snapreealandroid.diary.model.DiaryDao
import com.klevcsoo.snapreealandroid.diary.model.DiaryModel
import com.klevcsoo.snapreealandroid.snap.model.SnapDao
import com.klevcsoo.snapreealandroid.snap.model.SnapModel

@Database(entities = [DiaryModel::class, SnapModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao

    abstract fun snapDao(): SnapDao

    companion object {
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "snapreeal_db"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}
