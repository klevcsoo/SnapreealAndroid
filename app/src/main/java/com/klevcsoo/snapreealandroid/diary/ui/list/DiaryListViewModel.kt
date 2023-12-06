package com.klevcsoo.snapreealandroid.diary.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.klevcsoo.snapreealandroid.diary.DiaryRepository
import com.klevcsoo.snapreealandroid.diary.model.Diary

class DiaryListViewModel(repository: DiaryRepository) : ViewModel() {

    private val _diaries = repository.diaryList.asLiveData()
    val diaries: LiveData<List<Diary>>
        get() = _diaries

    companion object {
        class DiaryListViewModelFactory(private val repository: DiaryRepository) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DiaryListViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return DiaryListViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
