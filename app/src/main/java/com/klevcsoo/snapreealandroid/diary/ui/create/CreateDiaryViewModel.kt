package com.klevcsoo.snapreealandroid.diary.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klevcsoo.snapreealandroid.diary.DiaryRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

class CreateDiaryViewModel(private val repository: DiaryRepository) : ViewModel() {
    val diaryName: MutableLiveData<String> = MutableLiveData("")

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    fun create() = viewModelScope.launch {
        if (diaryName.value == null || diaryName.value!!.isEmpty()) {
            coroutineContext.job.cancel("Could not create diary: name empty")
        }

        _loading.value = true
        val job = viewModelScope.launch { repository.createDiary(diaryName.value!!) }
        job.invokeOnCompletion {
            if (it != null) coroutineContext.job.cancel("Could not create diary: $it")
            _loading.value = false
        }
    }

    companion object {
        const val TAG = "CreateDiaryViewModel"

        class CreateDiaryViewModelFactory(private val repository: DiaryRepository) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CreateDiaryViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return CreateDiaryViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
