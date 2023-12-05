package com.klevcsoo.snapreealandroid.diary.ui.details.video

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.klevcsoo.snapreealandroid.diary.model.Diary
import com.klevcsoo.snapreealandroid.media.MediaRepository
import kotlinx.coroutines.launch
import java.io.File

class DiaryVideoViewModel : ViewModel() {
    private val mediaRepository = MediaRepository()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _videoFile = MutableLiveData<File?>(null)
    val videoFile: LiveData<File?>
        get() = _videoFile

    fun generate(context: Context, scope: LifecycleCoroutineScope, diary: Diary) {
        _loading.value = true
        scope.launch {
            _videoFile.value = mediaRepository.generateDiaryVideo(context, diary)
            _loading.value = false
        }
    }

    fun saveToGallery(context: Context, diary: Diary) {
        if (_videoFile.value == null) {
            return
        }

        val now = System.currentTimeMillis()
        val publicVideoFile = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
            ),
            listOf("snapreeal", "generated", "diary_${diary.id}_$now").joinToString(File.separator)
        )

        _videoFile.value!!.copyTo(publicVideoFile, overwrite = true)
        MediaScannerConnection.scanFile(
            context,
            arrayOf(publicVideoFile.absolutePath),
            arrayOf("video/mp4"),
            null
        )
    }

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
