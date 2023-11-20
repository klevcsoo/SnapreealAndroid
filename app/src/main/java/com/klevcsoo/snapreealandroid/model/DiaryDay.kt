package com.klevcsoo.snapreealandroid.model

import java.io.Serializable

data class DiaryDay(
    val diary: Diary,
    val day: Long,
    var snap: Snap?
) : Serializable
