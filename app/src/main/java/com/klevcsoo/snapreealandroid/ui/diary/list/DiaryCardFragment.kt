package com.klevcsoo.snapreealandroid.ui.diary.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.klevcsoo.snapreealandroid.databinding.FragmentDiaryCardBinding
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.ui.diary.details.DiaryDetailsActivity
import com.klevcsoo.snapreealandroid.util.serializable

private const val ARG_DIARY = "diary"

class DiaryCardFragment : Fragment() {
    private var diary: Diary? = null

    private var _binding: FragmentDiaryCardBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            diary = it.serializable(ARG_DIARY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameText.text = diary?.name
        binding.openButton.setOnClickListener {
            val intent = Intent(activity, DiaryDetailsActivity::class.java)
            intent.putExtra("diary", diary)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(diary: Diary) =
            DiaryCardFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DIARY, diary)
                }
            }
    }
}
