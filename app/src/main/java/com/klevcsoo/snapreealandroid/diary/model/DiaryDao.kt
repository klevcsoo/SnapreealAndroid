package com.klevcsoo.snapreealandroid.diary.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diarymodel")
    fun list(): List<DiaryModel>

    @Query("SELECT * FROM diarymodel WHERE id = (:id) LIMIT 1")
    fun get(id: String): DiaryModel

    @Insert
    fun create(diaryModel: DiaryModel)
}
