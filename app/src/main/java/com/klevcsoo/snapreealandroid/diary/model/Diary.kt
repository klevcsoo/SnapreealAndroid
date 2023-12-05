package com.klevcsoo.snapreealandroid.diary.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.Instant
import java.util.Date

@Entity
data class Diary(
    @PrimaryKey(true) val id: Long,
    @ColumnInfo("created_at") val createdAt: Long,
    @ColumnInfo("name") val name: String
) : Serializable {
    constructor(name: String) : this(0, Date.from(Instant.now()).time, name)
}
