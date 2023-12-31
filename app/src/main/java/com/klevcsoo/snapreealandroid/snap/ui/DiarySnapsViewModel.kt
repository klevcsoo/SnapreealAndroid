package com.klevcsoo.snapreealandroid.snap.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.klevcsoo.snapreealandroid.diary.dto.DiaryDay
import com.klevcsoo.snapreealandroid.diary.model.Diary
import com.klevcsoo.snapreealandroid.snap.SnapRepository
import com.klevcsoo.snapreealandroid.snap.model.Snap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

class DiarySnapsViewModel(
    snapRepository: SnapRepository,
    private val diary: Diary,
    lifecycleOwner: LifecycleOwner
) :
    ViewModel() {
    private val snapList: LiveData<List<Snap>> = snapRepository.snapList(diary).asLiveData()

    private val _days: MutableLiveData<List<DiaryDay>> = MutableLiveData(listOf())
    val days: LiveData<List<DiaryDay>>
        get() = _days

    init {
        snapList.observe(lifecycleOwner) {
            viewModelScope.launch(Dispatchers.Main) {
                val mutableDays: MutableList<DiaryDay> = generateDays(diary).toMutableList()
                it.forEach { snap ->
                    val i = mutableDays.indexOfFirst { diaryDay -> diaryDay.day == snap.day }
                    if (i >= 0) mutableDays[i].snap = snap
                }
                _days.value = mutableDays
            }
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
        @Suppress("unused")
        private const val TAG = "DiarySnapViewModel"

        class DiarySnapsViewModelFactory(
            private val snapRepository: SnapRepository,
            private val diary: Diary,
            private val lifecycleOwner: LifecycleOwner
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DiarySnapsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return DiarySnapsViewModel(snapRepository, diary, lifecycleOwner) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
