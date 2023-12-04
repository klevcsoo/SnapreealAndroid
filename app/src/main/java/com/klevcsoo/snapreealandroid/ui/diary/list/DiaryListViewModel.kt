package com.klevcsoo.snapreealandroid.ui.diary.list

import android.net.Uri
import androidx.lifecycle.LiveData
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

    private val _userPhoto = MutableLiveData<Uri?>(null)
    val userPhoto: LiveData<Uri?>
        get() = _userPhoto

    private val _diaries = MutableLiveData<List<Diary>>(listOf())
    val diaries: LiveData<List<Diary>>
        get() = _diaries

    init {
        auth.addAuthStateListener { currentAuth ->
            if (currentAuth.currentUser == null) {
                _userPhoto.value = null
                _diaries.value = listOf()
            } else {
                _userPhoto.value = userRepository.getUserPhotoURL()
                diaryRepository.onDiaryList { _diaries.value = it }
            }
        }
    }
}
