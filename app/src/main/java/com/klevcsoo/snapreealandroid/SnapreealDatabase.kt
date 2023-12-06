package com.klevcsoo.snapreealandroid

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.klevcsoo.snapreealandroid.diary.model.Diary
import com.klevcsoo.snapreealandroid.diary.model.DiaryDao
import com.klevcsoo.snapreealandroid.snap.model.Snap
import com.klevcsoo.snapreealandroid.snap.model.SnapDao

@Database(entities = [Diary::class, Snap::class], version = 1, exportSchema = false)
abstract class SnapreealDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao

    abstract fun snapDao(): SnapDao

    companion object {
        private var instance: SnapreealDatabase? = null

        fun get(context: Context): SnapreealDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    SnapreealDatabase::class.java,
                    "snapreeal_db"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}
