package com.klevcsoo.snapreealandroid.diary.ui.details.days

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.klevcsoo.snapreealandroid.diary.DiaryRepository
import com.klevcsoo.snapreealandroid.diary.model.Diary

class DiaryDetailsViewModel(repository: DiaryRepository, diary: Diary) : ViewModel() {
    val diary: LiveData<Diary> = repository.diaryDetails(diary.id).asLiveData()

    companion object {
        class DiaryDetailsViewModelFactory(
            private val repository: DiaryRepository,
            private val diary: Diary
        ) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DiaryDetailsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return DiaryDetailsViewModel(repository, diary) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
