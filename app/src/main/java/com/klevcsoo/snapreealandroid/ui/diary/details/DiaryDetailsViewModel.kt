package com.klevcsoo.snapreealandroid.ui.diary.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.repository.DiaryRepository

class DiaryDetailsViewModel : ViewModel() {
    private val repository = DiaryRepository()

    private val _diary = MutableLiveData<Diary>()
    val diary: LiveData<Diary>
        get() = _diary

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    fun load(id: String) {
        _loading.value = true
        repository.onDiaryDetails(id) {
            _diary.value = it
            _loading.value = false
        }
    }
}
