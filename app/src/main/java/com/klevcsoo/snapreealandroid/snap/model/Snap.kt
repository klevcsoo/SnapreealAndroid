package com.klevcsoo.snapreealandroid.snap.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.klevcsoo.snapreealandroid.diary.model.Diary
import java.io.Serializable

@Entity
data class Snap(
    @PrimaryKey(true) val id: Long,
    @ColumnInfo("diary_id") val diaryId: Long,
    @ColumnInfo("day") val day: Long,
    @ColumnInfo("is_thumbnail_dark") val isThumbnailDark: Boolean,
    @ColumnInfo("media_length") val mediaLength: Int,
    @ColumnInfo("thumbnail_url") val thumbnailUrl: String,
    @ColumnInfo("video_url") val videoUrl: String
) : Serializable {
    constructor(
        diary: Diary,
        day: Long,
        isThumbnailDark: Boolean,
        thumbnailUrl: String,
        videoUrl: String
    ) : this(0, diary.id, day, isThumbnailDark, 3, thumbnailUrl, videoUrl)
}
