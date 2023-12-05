package com.klevcsoo.snapreealandroid.diary.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary")
    fun list(): List<Diary>

    @Query("SELECT * FROM diary WHERE id = (:id) LIMIT 1")
    fun get(id: Long): Diary

    @Insert
    fun create(diary: Diary)
}
