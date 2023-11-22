package com.klevcsoo.snapreealandroid.ui.diary.list

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.repository.DiaryRepository
import com.klevcsoo.snapreealandroid.repository.UserRepository
import com.klevcsoo.snapreealandroid.service.FirebaseService

class DiaryListViewModel : ViewModel() {
    private val auth = FirebaseService.instance.auth

    private val userRepository = UserRepository()
    private val diaryRepository = DiaryRepository()

    val userPhoto = MutableLiveData<Uri?>(null)
    val diaries = MutableLiveData<List<Diary>>(listOf())

    init {
        auth.addAuthStateListener { currentAuth ->
            if (currentAuth.currentUser == null) {
                userPhoto.value = null
                diaries.value = listOf()
            } else {
                userPhoto.value = userRepository.getUserPhotoURL()
                diaryRepository.onDiaryList { diaries.value = it }
            }
        }
    }
}
