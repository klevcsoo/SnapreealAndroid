package com.klevcsoo.snapreealandroid.diary.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary")
    fun list(): Flow<List<Diary>>

    @Query("SELECT * FROM diary WHERE id = (:id) LIMIT 1")
    fun get(id: Long): Flow<Diary>

    @Insert
    suspend fun create(diary: Diary)
}
