package com.klevcsoo.snapreealandroid.media

import android.content.Context
import android.util.Log
import com.klevcsoo.snapreealandroid.diary.dto.DiaryDay
import com.klevcsoo.snapreealandroid.diary.ui.details.video.DiaryVideoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MediaRepository {
    fun getSnapVideoFile(context: Context, diaryDay: DiaryDay): File {
        val localFilePath = listOf(
            "diary", diaryDay.diary.id, "${diaryDay.day}.mp4"
        ).joinToString(File.separator)

        return File(context.filesDir, localFilePath)
    }

    suspend fun generateDiaryVideo(context: Context, days: List<DiaryDay>): File {
        return withContext(Dispatchers.IO) {
            val videos = days.map {
                getSnapVideoFile(context, it)
            }

            Log.d(DiaryVideoViewModel.TAG, "Generating video...")
            val videoFile = concatVideoFiles(context, videos)
            Log.d(DiaryVideoViewModel.TAG, "Video generated.")

            return@withContext videoFile
        }
    }
}
