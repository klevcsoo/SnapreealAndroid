package com.klevcsoo.snapreealandroid.ui.diary.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.klevcsoo.snapreealandroid.R

class DiarySnapsFragment : Fragment() {

    companion object {
        fun newInstance() = DiarySnapsFragment()
    }

    private lateinit var viewModel: DiarySnapsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diary_snaps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DiarySnapsViewModel::class.java]
    }
}
