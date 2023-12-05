package com.klevcsoo.snapreealandroid.diary.ui.details.days

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.klevcsoo.snapreealandroid.diary.DiaryRepository
import com.klevcsoo.snapreealandroid.diary.model.DiaryModel

class DiaryDetailsViewModel : ViewModel() {
    private val repository = DiaryRepository()

    private val _diary = MutableLiveData<DiaryModel>()
    val diary: LiveData<DiaryModel>
        get() = _diary

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    fun load(context: Context, id: String) {
        _loading.value = true
        repository.onDiaryDetails(context, id) {
            _diary.value = it
            _loading.value = false
        }
    }
}
