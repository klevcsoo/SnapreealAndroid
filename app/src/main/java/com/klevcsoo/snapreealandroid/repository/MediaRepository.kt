package com.klevcsoo.snapreealandroid.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.TaskCompletionSource
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.model.DiaryDay
import com.klevcsoo.snapreealandroid.ui.diary.details.video.DiaryVideoViewModel
import com.klevcsoo.snapreealandroid.util.concatVideoFiles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MediaRepository {
    private val diaryRepository = DiaryRepository()

    suspend fun getSnapVideoFile(context: Context, diaryDay: DiaryDay): File {
        val localFilePath = listOf(
            "diary", diaryDay.diary.id, "${diaryDay.day}.mp4"
        ).joinToString(File.separator)

        val cachedFile = File(context.cacheDir, localFilePath)
        return if (cachedFile.exists()) cachedFile else TaskCompletionSource<File>().apply {
            URL(diaryDay.snap!!.videoUrl).openStream().use { input ->
                cachedFile.parentFile?.mkdirs()
                FileOutputStream(cachedFile).use { output ->
                    input.copyTo(output)
                    setResult(cachedFile)
                }
            }
        }.task.await()
    }

    suspend fun generateDiaryVideo(context: Context, diary: Diary): File {
        return withContext(Dispatchers.IO) {
            val snaps = diaryRepository.getDiarySnapList(diary)
            val videos = snaps.map {
                getSnapVideoFile(context, DiaryDay(diary, it.day, it))
            }

            Log.d(DiaryVideoViewModel.TAG, "Generating video...")
            val videoFile = concatVideoFiles(context, videos)
            Log.d(DiaryVideoViewModel.TAG, "Video generated.")

            return@withContext videoFile
        }
    }
}
