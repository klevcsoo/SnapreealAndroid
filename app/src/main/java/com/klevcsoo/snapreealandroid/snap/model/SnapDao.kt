package com.klevcsoo.snapreealandroid.snap.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SnapDao {
    @Query("SELECT * FROM snap WHERE diary_id = (:diaryId)")
    fun list(diaryId: Long): Flow<List<Snap>>

    @Query("SELECT * FROM snap WHERE id = (:id) LIMIT 1")
    fun get(id: Long): Flow<Snap?>

    @Query("SELECT * FROM snap WHERE diary_id = (:diaryId) AND day = (:day)")
    fun getByDay(diaryId: Long, day: Long): Flow<Snap?>

    @Query("SELECT * FROM snap WHERE diary_id = (:diaryId) ORDER BY day DESC LIMIT 1")
    fun getLatest(diaryId: Long): Flow<Snap?>

    @Insert
    suspend fun create(snap: Snap)

    @Delete
    suspend fun delete(snap: Snap)
}
