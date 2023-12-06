package com.klevcsoo.snapreealandroid.diary.ui.list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.klevcsoo.snapreealandroid.R
import com.klevcsoo.snapreealandroid.SnapreealApplication
import com.klevcsoo.snapreealandroid.databinding.FragmentDiaryCardBinding
import com.klevcsoo.snapreealandroid.diary.model.Diary
import com.klevcsoo.snapreealandroid.diary.ui.details.days.DiaryDetailsActivity
import com.klevcsoo.snapreealandroid.media.dimmedFilter
import com.klevcsoo.snapreealandroid.util.serializable
import java.time.LocalDate

private const val ARG_DIARY = "diary"

class DiaryCardFragment : Fragment() {
    private var _binding: FragmentDiaryCardBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DiaryCardViewModel> {
        DiaryCardViewModel.Companion.DiaryCardViewModelFactory(
            (requireActivity().application as SnapreealApplication).snapRepository,
            requireArguments().serializable<Diary>(ARG_DIARY)!!
        )
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

        binding.nameText.text = viewModel.diary.name
        binding.backgroundImage.setOnClickListener {
            val intent = Intent(activity, DiaryDetailsActivity::class.java)
            intent.putExtra("diary", viewModel.diary)
            startActivity(intent)
        }

        viewModel.latestSnap.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.descriptionText.text = LocalDate.ofEpochDay(it.day).toString()
                binding.backgroundImage.setImageURI(Uri.parse(it.thumbnailUrl))
                binding.backgroundImage.colorFilter = dimmedFilter()
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
