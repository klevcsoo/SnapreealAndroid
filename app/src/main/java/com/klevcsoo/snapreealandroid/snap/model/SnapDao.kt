package com.klevcsoo.snapreealandroid.snap.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SnapDao {
    @Query("SELECT * FROM snap WHERE diary_id = (:diaryId)")
    fun list(diaryId: Long): List<Snap>

    @Query("SELECT * FROM snap WHERE id = (:id) LIMIT 1")
    fun get(id: Long): Snap

    @Query("SELECT * FROM snap WHERE diary_id = (:diaryId) ORDER BY day DESC LIMIT 1")
    fun getLatest(diaryId: Long): Snap?

    @Insert
    fun create(snap: Snap)

    @Delete
    fun delete(snap: Snap)
}
