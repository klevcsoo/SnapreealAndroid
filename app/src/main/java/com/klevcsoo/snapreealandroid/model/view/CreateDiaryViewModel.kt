package com.klevcsoo.snapreealandroid.model.view

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klevcsoo.snapreealandroid.repository.DiaryRepository
import kotlinx.coroutines.launch

class CreateDiaryViewModel : ViewModel() {
    private val repository = DiaryRepository()

    val diaryName: MutableLiveData<String> = MutableLiveData("")
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)

    fun create(onCreated: () -> Unit) {
        Log.d(TAG, "Current name: ${diaryName.value}")
        if (diaryName.value == null || diaryName.value!!.isEmpty()) {
            Log.w(TAG, "Could not create diary: name empty")
            return
        }

        loading.value = true
        val job = viewModelScope.launch { repository.createDiary(diaryName.value!!) }
        job.invokeOnCompletion {
            if (it != null) {
                Log.w(TAG, "Could not create diary", it)
            } else {
                onCreated()
            }

            loading.value = false
        }
    }

    companion object {
        const val TAG = "CreateDiaryViewModel"
    }
}
