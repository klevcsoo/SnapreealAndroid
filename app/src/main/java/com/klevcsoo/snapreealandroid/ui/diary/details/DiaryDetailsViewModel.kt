package com.klevcsoo.snapreealandroid.ui.diary.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.repository.DiaryRepository

class DiaryDetailsViewModel : ViewModel() {
    private val repository = DiaryRepository()

    val diary = MutableLiveData<Diary>()
    val loading = MutableLiveData(false)

    fun load(id: String) {
        loading.value = true
        repository.onDiaryDetails(id) {
            diary.value = it
            loading.value = false
        }
    }
}
