package com.klevcsoo.snapreealandroid.diary.dto

import com.klevcsoo.snapreealandroid.diary.model.DiaryModel
import com.klevcsoo.snapreealandroid.snap.model.SnapModel
import java.io.Serializable

data class DiaryDay(
    val diary: DiaryModel,
    val day: Long,
    var snap: SnapModel?
) : Serializable
