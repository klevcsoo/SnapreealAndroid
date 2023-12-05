package com.klevcsoo.snapreealandroid.diary.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.klevcsoo.snapreealandroid.R
import com.klevcsoo.snapreealandroid.databinding.FragmentDiaryCardBinding
import com.klevcsoo.snapreealandroid.diary.DiaryRepository
import com.klevcsoo.snapreealandroid.diary.model.DiaryModel
import com.klevcsoo.snapreealandroid.diary.ui.details.days.DiaryDetailsActivity
import com.klevcsoo.snapreealandroid.util.serializable
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val ARG_DIARY = "diary"

class DiaryCardFragment : Fragment() {
    private val repository = DiaryRepository()

    private var diary: DiaryModel? = null

    private var _binding: FragmentDiaryCardBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            diary = it.serializable<DiaryModel>(ARG_DIARY)
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
        binding.backgroundImage.setOnClickListener {
            val intent = Intent(activity, DiaryDetailsActivity::class.java)
            intent.putExtra("diary", diary)
            startActivity(intent)
        }

        lifecycleScope.launch {
            repository.getDiaryLatestSnap(requireContext(), diary!!).let {
                if (it != null) {
                    binding.descriptionText.text = LocalDate.ofEpochDay(it.day).toString()
                    Picasso.get().load(it.thumbnailUrl).into(binding.backgroundImage)
                    if (it.isThumbnailDark) {
                        binding.nameText.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.white)
                        )
                        binding.descriptionText.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.white)
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(diary: DiaryModel) =
            DiaryCardFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DIARY, diary)
                }
            }
    }
}
