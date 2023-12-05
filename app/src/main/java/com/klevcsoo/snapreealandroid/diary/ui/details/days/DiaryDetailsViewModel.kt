package com.klevcsoo.snapreealandroid.diary.ui.details.days

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.klevcsoo.snapreealandroid.diary.DiaryRepository
import com.klevcsoo.snapreealandroid.diary.model.Diary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiaryDetailsViewModel : ViewModel() {
    private val repository = DiaryRepository()

    private val _diary = MutableLiveData<Diary>()
    val diary: LiveData<Diary>
        get() = _diary

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    fun load(context: Context, id: Long) {
        _loading.value = true
        CoroutineScope(Dispatchers.Main).launch {
            _diary.value = repository.getDiaryDetails(context, id)
            _loading.value = false
        }
    }
}
