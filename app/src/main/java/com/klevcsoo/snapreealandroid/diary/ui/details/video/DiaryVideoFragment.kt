package com.klevcsoo.snapreealandroid.diary.ui.details.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.klevcsoo.snapreealandroid.SnapreealApplication
import com.klevcsoo.snapreealandroid.databinding.FragmentDiaryVideoBinding
import com.klevcsoo.snapreealandroid.diary.model.Diary
import com.klevcsoo.snapreealandroid.util.serializable

class DiaryVideoFragment : Fragment() {
    private var _binding: FragmentDiaryVideoBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DiaryVideoViewModel> {
        DiaryVideoViewModel.Companion.DiaryVideoViewModelFactory(
            (requireActivity().application as SnapreealApplication).mediaRepository,
            (requireActivity().application as SnapreealApplication).snapRepository,
            requireArguments().serializable<Diary>(ARG_DIARY)!!,
            viewLifecycleOwner
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryVideoBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.videoFile.observe(viewLifecycleOwner) { file ->
            if (file != null) {
                binding.saveButton.setOnClickListener {
                    viewModel.saveToGallery(requireContext())
                    Toast.makeText(requireContext(), "saved to gallery!", Toast.LENGTH_LONG)
                        .show()
                }

                binding.videoPreview.setVideoPath(file.path)
                binding.videoPreview.setOnPreparedListener { it.isLooping = true }
                binding.videoPreview.start()
            } else {
                binding.saveButton.setOnClickListener {
                    viewModel.generate(requireContext())
                }

                binding.videoPreview.stopPlayback()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_DIARY = "diary"

        fun newInstance(diary: Diary) = DiaryVideoFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_DIARY, diary)
            }
        }
    }
}
