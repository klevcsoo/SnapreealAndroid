package com.klevcsoo.snapreealandroid

import android.app.Application
import com.klevcsoo.snapreealandroid.diary.DiaryRepository
import com.klevcsoo.snapreealandroid.media.MediaRepository
import com.klevcsoo.snapreealandroid.snap.SnapRepository

class SnapreealApplication : Application() {

    private val database by lazy { SnapreealDatabase.get(this) }
    val diaryRepository by lazy { DiaryRepository(database.diaryDao()) }
    val snapRepository by lazy { SnapRepository(database.snapDao()) }
    val mediaRepository by lazy { MediaRepository() }
}
