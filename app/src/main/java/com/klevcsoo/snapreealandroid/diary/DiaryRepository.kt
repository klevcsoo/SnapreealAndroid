package com.klevcsoo.snapreealandroid.diary

import android.content.Context
import android.util.Log
import com.klevcsoo.snapreealandroid.AppDatabase
import com.klevcsoo.snapreealandroid.diary.dto.DiaryDay
import com.klevcsoo.snapreealandroid.diary.model.Diary
import com.klevcsoo.snapreealandroid.media.calculateIsThumbnailDark
import com.klevcsoo.snapreealandroid.media.generateThumbnailFor
import com.klevcsoo.snapreealandroid.snap.model.Snap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File

class DiaryRepository {
    suspend fun getDiaryList(context: Context): List<Diary> {
        return CoroutineScope(Dispatchers.IO).async {
            AppDatabase.get(context).diaryDao().list()
        }.await()
    }

    suspend fun getDiaryDetails(context: Context, id: Long): Diary {
        return CoroutineScope(Dispatchers.IO).async {
            AppDatabase.get(context).diaryDao().get(id)
        }.await()
    }

    suspend fun getDiarySnapList(context: Context, diary: Diary): List<Snap> {
        return withContext(Dispatchers.IO) {
            AppDatabase.get(context).snapDao().list(diary.id)
        }
    }

    suspend fun getDiarySnapList(context: Context, id: Long): List<Snap> {
        return CoroutineScope(Dispatchers.IO).async {
            AppDatabase.get(context).snapDao().list(id)
        }.await()
    }

    suspend fun createDiary(context: Context, name: String) {
        withContext(Dispatchers.IO) {
            AppDatabase.get(context).diaryDao().create(Diary(name))
        }
    }

    suspend fun uploadSnap(context: Context, diary: Diary, day: Long, videoFile: File) {
        Log.d(TAG, "Generating snap thumbnail...")
        val thumbnailFile = generateThumbnailFor(videoFile)
        val isDark = calculateIsThumbnailDark(thumbnailFile)

        withContext(Dispatchers.IO) {
            AppDatabase.get(context).snapDao()
                .create(Snap(diary, day, isDark, thumbnailFile.path, videoFile.path))
        }

        Log.d(TAG, "Snap added!")
    }

    suspend fun deleteSnap(context: Context, diaryDay: DiaryDay) {
        withContext(Dispatchers.IO) {
            if (diaryDay.snap != null) {
                File(diaryDay.snap!!.videoUrl).delete()
                File(diaryDay.snap!!.thumbnailUrl).delete()
                AppDatabase.get(context).snapDao().delete(diaryDay.snap!!)
            }
        }
    }

    suspend fun getDiaryLatestSnap(context: Context, diary: Diary): Snap? {
        return withContext(Dispatchers.IO) {
            AppDatabase.get(context).snapDao().getLatest(diary.id)
        }
    }

    companion object {
        const val TAG = "DiaryRepository"
    }
}
