package com.klevcsoo.snapreealandroid.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.klevcsoo.snapreealandroid.databinding.FragmentDiaryCardBinding
import com.klevcsoo.snapreealandroid.model.Diary

private const val ARG_DIARY_ID = "diaryId"
private const val ARG_DIARY_NAME = "diaryName"

class DiaryCardFragment : Fragment() {
    private var diaryId: String? = null
    private var diaryName: String? = null

    private var _binding: FragmentDiaryCardBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            diaryId = it.getString(ARG_DIARY_ID)
            diaryName = it.getString(ARG_DIARY_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryCardBinding.inflate(inflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.fragment_diary_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameText.text = diaryName
        binding.openButton.setOnClickListener {
            Log.d(TAG, "$diaryId")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "DiaryCardFragment"

        @JvmStatic
        fun newInstance(diary: Diary) =
            DiaryCardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DIARY_ID, diary.id)
                    putString(ARG_DIARY_NAME, diary.name)
                }
            }
    }
}
