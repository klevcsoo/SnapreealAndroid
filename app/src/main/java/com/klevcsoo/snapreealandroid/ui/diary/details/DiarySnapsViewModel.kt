package com.klevcsoo.snapreealandroid.ui.diary.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.model.Snap
import com.klevcsoo.snapreealandroid.repository.DiaryRepository

class DiarySnapsViewModel() : ViewModel() {
    private val diary: MutableLiveData<Diary> = MutableLiveData()
    private val snaps: MutableLiveData<List<Snap>> = MutableLiveData(listOf())
    private val loading: MutableLiveData<Boolean> = MutableLiveData(false)

    private val repository = DiaryRepository()

    fun load(id: String) {
        loading.value = true

        repository.onDiaryDetails(id) {
            diary.value = it
            loading.value = false
        }
        repository.onDiarySnapList(id) {
            snaps.value = it
        }
    }
}
