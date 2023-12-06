package com.klevcsoo.snapreealandroid.snap.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klevcsoo.snapreealandroid.diary.dto.DiaryDay
import com.klevcsoo.snapreealandroid.snap.SnapRepository
import kotlinx.coroutines.launch
import java.io.File

class SnapInspectorViewModel(
    private val repository: SnapRepository,
    val diaryDay: DiaryDay,
    val snapFile: File,
    val ignoreSnap: Boolean
) : ViewModel() {
    fun uploadSnap() {
        viewModelScope.launch {
            repository.createSnap(diaryDay.diary, diaryDay.day, snapFile)
        }
    }

    fun deleteSnap() {
        viewModelScope.launch {
            repository.deleteSnap(diaryDay)
        }
    }

    companion object {
        class SnapInspectorViewModelFactory(
            private val repository: SnapRepository,
            private val diaryDay: DiaryDay,
            private val snapFile: File,
            private val ignoreSnap: Boolean
        ) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SnapInspectorViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return SnapInspectorViewModel(repository, diaryDay, snapFile, ignoreSnap) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
