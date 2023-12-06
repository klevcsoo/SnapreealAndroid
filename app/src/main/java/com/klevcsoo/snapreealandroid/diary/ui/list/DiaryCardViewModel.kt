package com.klevcsoo.snapreealandroid.diary.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.klevcsoo.snapreealandroid.diary.model.Diary
import com.klevcsoo.snapreealandroid.snap.SnapRepository
import com.klevcsoo.snapreealandroid.snap.model.Snap

class DiaryCardViewModel(snapRepository: SnapRepository, val diary: Diary) :
    ViewModel() {
    val latestSnap: LiveData<Snap?> = snapRepository.latestSnap(diary).asLiveData()

    companion object {
        class DiaryCardViewModelFactory(
            private val snapRepository: SnapRepository,
            private val diary: Diary
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DiaryCardViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return DiaryCardViewModel(snapRepository, diary) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
