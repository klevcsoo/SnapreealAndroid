package com.klevcsoo.snapreealandroid.util

import android.content.Context
import com.klevcsoo.snapreealandroid.model.DiaryDay
import java.io.File
import java.io.FileOutputStream
import java.net.URL

fun getSnapVideoFile(context: Context, diaryDay: DiaryDay, callback: (file: File) -> Unit) {
    val localFilePath = listOf(
        "snap", diaryDay.diary.id, "${diaryDay.day}.mp4"
    ).joinToString(File.separator)

    val cachedFile = File(context.filesDir, localFilePath)
    if (cachedFile.exists()) {
        callback(cachedFile)
        return
    }

    URL(diaryDay.snap!!.videoUrl).openStream().use { input ->
        FileOutputStream(File(localFilePath)).use { output ->
            input.copyTo(output)
            callback(File(localFilePath))
        }
    }
}
