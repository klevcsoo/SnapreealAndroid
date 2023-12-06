package com.klevcsoo.snapreealandroid.snap.ui.create

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.klevcsoo.snapreealandroid.diary.dto.DiaryDay
import com.klevcsoo.snapreealandroid.media.MediaRepository
import java.io.File

class CreateSnapViewModel(private val mediaRepository: MediaRepository, val diaryDay: DiaryDay) :
    ViewModel() {

    fun getFile(context: Context): File {
        return mediaRepository.getSnapVideoFile(context, diaryDay)
    }

    companion object {
        class CreateSnapViewModelFactory(
            private val repository: MediaRepository,
            private val diaryDay: DiaryDay
        ) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CreateSnapViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return CreateSnapViewModel(repository, diaryDay) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
