package com.klevcsoo.snapreealandroid.snap.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SnapDao {
    @Query("SELECT * FROM snapmodel WHERE diary_id = (:diaryId)")
    fun list(diaryId: String): List<SnapModel>

    @Query("SELECT * FROM snapmodel WHERE id = (:id) LIMIT 1")
    fun get(id: String): SnapModel

    @Query("SELECT * FROM snapmodel WHERE diary_id = (:diaryId) ORDER BY day DESC LIMIT 1")
    fun getLatest(diaryId: String): SnapModel?

    @Insert
    fun create(snapModel: SnapModel)

    @Delete
    fun delete(snapModel: SnapModel)
}
