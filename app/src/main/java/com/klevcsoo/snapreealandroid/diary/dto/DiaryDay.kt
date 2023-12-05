package com.klevcsoo.snapreealandroid.diary.dto

import com.klevcsoo.snapreealandroid.diary.model.Diary
import com.klevcsoo.snapreealandroid.snap.model.Snap
import java.io.Serializable

data class DiaryDay(
    val diary: Diary,
    val day: Long,
    var snap: Snap?
) : Serializable
