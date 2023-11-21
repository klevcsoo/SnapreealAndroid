package com.klevcsoo.snapreealandroid.ui.diary.details.video

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.repository.MediaRepository
import kotlinx.coroutines.launch
import java.io.File

class DiaryVideoViewModel : ViewModel() {
    private val mediaRepository = MediaRepository()

    val loading = MutableLiveData(false)
    val videoFile = MutableLiveData<File?>(null)

    fun generate(context: Context, scope: LifecycleCoroutineScope, diary: Diary) {
        loading.value = true
        scope.launch {
            videoFile.value = mediaRepository.generateDiaryVideo(context, diary)
            loading.value = false
        }
    }

    fun saveToGallery() {}

    companion object {
        const val TAG = "DiaryVideoViewModel"

        class DiaryVideoViewModelFactory : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DiaryVideoViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return DiaryVideoViewModel() as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
