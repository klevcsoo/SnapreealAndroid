package com.klevcsoo.snapreealandroid.diary.ui.details.video

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.klevcsoo.snapreealandroid.diary.dto.DiaryDay
import com.klevcsoo.snapreealandroid.diary.model.Diary
import com.klevcsoo.snapreealandroid.media.MediaRepository
import com.klevcsoo.snapreealandroid.snap.SnapRepository
import kotlinx.coroutines.launch
import java.io.File

class DiaryVideoViewModel(
    private val mediaRepository: MediaRepository,
    snapRepository: SnapRepository,
    private val diary: Diary,
    lifecycleOwner: LifecycleOwner
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _videoFile = MutableLiveData<File?>(null)
    val videoFile: LiveData<File?> get() = _videoFile

    private val snaps = snapRepository.snapList(diary).asLiveData()
    private val days = MutableLiveData<List<DiaryDay>>(listOf())

    init {
        snaps.observe(lifecycleOwner) { currentSnaps ->
            days.value = currentSnaps.map { DiaryDay(diary, it.day, it) }
        }
    }

    fun generate(context: Context) {
        _loading.value = true
        viewModelScope.launch {
            if (days.value == null) {
                Log.w(TAG, "No snaps found")
            } else {
                _videoFile.value = mediaRepository.generateDiaryVideo(context, days.value!!)
            }

            _loading.value = false
        }
    }

    fun saveToGallery(context: Context) {
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

        class DiaryVideoViewModelFactory(
            private val mediaRepository: MediaRepository,
            private val snapRepository: SnapRepository,
            private val diary: Diary,
            private val lifecycleOwner: LifecycleOwner
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DiaryVideoViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return DiaryVideoViewModel(
                        mediaRepository,
                        snapRepository,
                        diary,
                        lifecycleOwner
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
