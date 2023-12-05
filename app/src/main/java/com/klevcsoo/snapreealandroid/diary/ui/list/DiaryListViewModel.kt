package com.klevcsoo.snapreealandroid.diary.ui.list

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.klevcsoo.snapreealandroid.diary.DiaryRepository
import com.klevcsoo.snapreealandroid.diary.model.DiaryModel

class DiaryListViewModel : ViewModel() {
    private val diaryRepository = DiaryRepository()

    private val _diaries = MutableLiveData<List<DiaryModel>>(listOf())
    val diaries: LiveData<List<DiaryModel>>
        get() = _diaries

    fun load(context: Context) {
        diaryRepository.onDiaryList(context) { _diaries.value = it }
    }
}
