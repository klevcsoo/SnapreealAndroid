package com.klevcsoo.snapreealandroid.diary

import android.content.Context
import android.util.Log
import com.klevcsoo.snapreealandroid.AppDatabase
import com.klevcsoo.snapreealandroid.diary.dto.DiaryDay
import com.klevcsoo.snapreealandroid.diary.model.DiaryModel
import com.klevcsoo.snapreealandroid.media.calculateIsThumbnailDark
import com.klevcsoo.snapreealandroid.media.generateThumbnailFor
import com.klevcsoo.snapreealandroid.snap.model.SnapModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DiaryRepository {
    fun onDiaryList(context: Context, listener: (diaries: List<DiaryModel>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            listener(AppDatabase.get(context).diaryDao().list())
        }
    }

    fun onDiaryDetails(context: Context, id: String, listener: (diary: DiaryModel?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            listener(AppDatabase.get(context).diaryDao().get(id))
        }
    }

    suspend fun getDiarySnapList(context: Context, diary: DiaryModel): List<SnapModel> {
        return withContext(Dispatchers.IO) {
            AppDatabase.get(context).snapDao().list(diary.id)
        }
    }

    fun onDiarySnapList(context: Context, id: String, listener: (snaps: List<SnapModel>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            listener(AppDatabase.get(context).snapDao().list(id))
        }
    }

    suspend fun createDiary(context: Context, name: String) {
        withContext(Dispatchers.IO) {
            AppDatabase.get(context).diaryDao().create(DiaryModel(name))
        }
    }

    suspend fun uploadSnap(context: Context, diary: DiaryModel, day: Long, videoFile: File) {
        Log.d(TAG, "Generating snap thumbnail...")
        val thumbnailFile = generateThumbnailFor(videoFile)
        val isDark = calculateIsThumbnailDark(thumbnailFile)

        withContext(Dispatchers.IO) {
            AppDatabase.get(context).snapDao()
                .create(SnapModel(diary, day, isDark, thumbnailFile.path, videoFile.path))
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

    suspend fun getDiaryLatestSnap(context: Context, diary: DiaryModel): SnapModel? {
        return withContext(Dispatchers.IO) {
            AppDatabase.get(context).snapDao().getLatest(diary.id)
        }
    }

    companion object {
        const val TAG = "DiaryRepository"
    }
}
