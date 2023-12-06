package com.klevcsoo.snapreealandroid.snap

import android.util.Log
import com.klevcsoo.snapreealandroid.diary.DiaryRepository
import com.klevcsoo.snapreealandroid.diary.dto.DiaryDay
import com.klevcsoo.snapreealandroid.diary.model.Diary
import com.klevcsoo.snapreealandroid.media.calculateIsThumbnailDark
import com.klevcsoo.snapreealandroid.media.generateThumbnailFor
import com.klevcsoo.snapreealandroid.snap.model.Snap
import com.klevcsoo.snapreealandroid.snap.model.SnapDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class SnapRepository(private val snapDao: SnapDao) {
    fun snapList(diary: Diary): Flow<List<Snap>> {
        return snapDao.list(diary.id)
    }

    fun latestSnap(diary: Diary): Flow<Snap?> {
        return snapDao.getLatest(diary.id)
    }

    suspend fun createSnap(diary: Diary, day: Long, videoFile: File) {
        withContext(Dispatchers.IO) {
            Log.d(DiaryRepository.TAG, "Generating snap thumbnail...")
            val thumbnailFile = generateThumbnailFor(videoFile)
            val isDark = calculateIsThumbnailDark(thumbnailFile)

            withContext(Dispatchers.IO) {
                val snap = snapDao.getByDay(diary.id, day)
                if (snap != null) snapDao.delete(snap)

                snapDao.create(Snap(diary, day, isDark, thumbnailFile.path, videoFile.path))
            }

            Log.d(DiaryRepository.TAG, "Snap added!")
        }
    }

    suspend fun deleteSnap(diaryDay: DiaryDay) {
        withContext(Dispatchers.IO) {
            if (diaryDay.snap != null) {
                File(diaryDay.snap!!.videoUrl).delete()
                File(diaryDay.snap!!.thumbnailUrl).delete()
                snapDao.delete(diaryDay.snap!!)
            }
        }
    }
}
