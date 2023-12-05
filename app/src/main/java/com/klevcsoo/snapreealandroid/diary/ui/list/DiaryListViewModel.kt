package com.klevcsoo.snapreealandroid.diary.ui.list

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.klevcsoo.snapreealandroid.diary.DiaryRepository
import com.klevcsoo.snapreealandroid.diary.model.Diary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiaryListViewModel : ViewModel() {
    private val diaryRepository = DiaryRepository()

    private val _diaries = MutableLiveData<List<Diary>>(listOf())
    val diaries: LiveData<List<Diary>>
        get() = _diaries

    fun load(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            _diaries.value = diaryRepository.getDiaryList(context)
        }
    }
}
