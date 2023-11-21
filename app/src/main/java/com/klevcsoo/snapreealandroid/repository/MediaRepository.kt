package com.klevcsoo.snapreealandroid.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
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

    fun getSnapVideoFile(context: Context, diaryDay: DiaryDay): Task<File> {
        val res = TaskCompletionSource<File>()
        getSnapVideoFile(context, diaryDay) { res.setResult(it) }
        return res.task
    }

    suspend fun generateDiaryVideo(context: Context, diary: Diary): File {
        return withContext(Dispatchers.IO) {
            val snaps = diaryRepository.getDiarySnapList(diary)
            val videos = snaps.map {
                getSnapVideoFile(context, DiaryDay(diary, it.day, it)).await()
            }

            Log.d(DiaryVideoViewModel.TAG, "Generating video...")
            val videoFile = concatVideoFiles(context, videos)
            Log.d(DiaryVideoViewModel.TAG, "Video generated.")

            return@withContext videoFile
        }
    }
}
