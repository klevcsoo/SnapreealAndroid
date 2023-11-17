package com.klevcsoo.snapreealandroid.model.view

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.repository.DiaryRepository
import com.klevcsoo.snapreealandroid.repository.UserRepository

class DiaryListViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val diaryRepository = DiaryRepository()

    fun getPhotoURL(): Uri? {
        return userRepository.getUserPhotoURL()
    }

    fun onList(listener: (diaries: List<Diary>) -> Unit) {
        diaryRepository.onDiaryList(listener)
    }
}
