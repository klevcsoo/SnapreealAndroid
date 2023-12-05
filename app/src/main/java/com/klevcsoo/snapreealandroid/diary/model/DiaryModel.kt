package com.klevcsoo.snapreealandroid.diary.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.Instant
import java.util.Date

@Entity
data class DiaryModel(
    @PrimaryKey(true) val id: String,
    @ColumnInfo("created_at") val createdAt: Date,
    @ColumnInfo("name") val name: String
) : Serializable {
    constructor(name: String) : this("", Date.from(Instant.now()), name)
}
