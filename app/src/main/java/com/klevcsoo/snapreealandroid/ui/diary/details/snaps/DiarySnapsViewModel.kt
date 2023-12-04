package com.klevcsoo.snapreealandroid.ui.diary.details.snaps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.model.DiaryDay
import com.klevcsoo.snapreealandroid.repository.DiaryRepository
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

class DiarySnapsViewModel : ViewModel() {
    private val _days: MutableLiveData<List<DiaryDay>> = MutableLiveData(listOf())
    val days: LiveData<List<DiaryDay>>
        get() = _days

    private val repository = DiaryRepository()

    fun load(diary: Diary) {
        repository.onDiarySnapList(diary.id) {
            val mutableDays: MutableList<DiaryDay> = generateDays(diary).toMutableList()
            it.forEach { snap ->
                val i = mutableDays.indexOfFirst { diaryDay -> diaryDay.day == snap.day }
                if (i >= 0) mutableDays[i].snap = snap
            }
            _days.value = mutableDays
        }
    }

    private fun generateDays(diary: Diary): List<DiaryDay> {
        val calendar = Calendar.getInstance()
        val list = mutableListOf<Date>()

        for (i in 0 downTo -99) {
            calendar.add(Calendar.DAY_OF_YEAR, i)
            list.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, -i)
        }

        return list.map { date ->
            DiaryDay(
                diary, Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault())
                    .toLocalDate().toEpochDay(), null
            )
        }
    }

    companion object {
        class DiarySnapsViewModelFactory : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DiarySnapsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return DiarySnapsViewModel() as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
