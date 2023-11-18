package com.klevcsoo.snapreealandroid.ui.diary.details.snaps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.klevcsoo.snapreealandroid.model.Snap
import com.klevcsoo.snapreealandroid.repository.DiaryRepository
import java.util.Calendar
import java.util.Date

class DiarySnapsViewModel() : ViewModel() {
    val snaps: MutableLiveData<List<Snap>> = MutableLiveData(listOf())
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val dates: MutableLiveData<List<Date>> = MutableLiveData(listOf())

    private val repository = DiaryRepository()

    init {
        dates.value = generateDates()
    }

    fun load(id: String) {
        loading.value = true
        repository.onDiarySnapList(id) {
            snaps.value = it
            loading.value = false
        }
    }

    private fun generateDates(): List<Date> {
        val calendar = Calendar.getInstance()
        val list = mutableListOf<Date>()

        for (i in 0 downTo -99) {
            calendar.add(Calendar.DAY_OF_YEAR, i)
            list.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, -i)
        }

        return list.reversed()
    }
}
