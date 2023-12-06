package com.klevcsoo.snapreealandroid.diary

import com.klevcsoo.snapreealandroid.diary.model.Diary
import com.klevcsoo.snapreealandroid.diary.model.DiaryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DiaryRepository(private val diaryDao: DiaryDao) {
    val diaryList = diaryDao.list()

    fun diaryDetails(id: Long): Flow<Diary> {
        return diaryDao.get(id)
    }

    suspend fun createDiary(name: String) {
        withContext(Dispatchers.IO) {
            diaryDao.create(Diary(name))
        }
    }

    companion object {
        const val TAG = "DiaryRepository"
    }
}
