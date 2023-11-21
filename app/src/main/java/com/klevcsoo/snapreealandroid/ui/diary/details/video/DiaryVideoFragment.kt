package com.klevcsoo.snapreealandroid.ui.diary.details.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.klevcsoo.snapreealandroid.databinding.FragmentDiaryVideoBinding
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.util.serializable

class DiaryVideoFragment : Fragment() {
    private var _binding: FragmentDiaryVideoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DiaryVideoViewModel by viewModels<DiaryVideoViewModel> {
        DiaryVideoViewModel.Companion.DiaryVideoViewModelFactory()
    }

    private lateinit var diary: Diary

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            diary = it.serializable<Diary>(ARG_DIARY)!!
        }

        _binding = FragmentDiaryVideoBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.videoFile.observe(viewLifecycleOwner) { file ->
            if (file != null) {
                binding.saveButton.setOnClickListener {
                    viewModel.saveToGallery(requireContext(), diary)
                    Toast.makeText(requireContext(), "saved to gallery!", Toast.LENGTH_LONG)
                        .show()
                }

                binding.videoPreview.setVideoPath(file.path)
                binding.videoPreview.setOnPreparedListener { it.isLooping = true }
                binding.videoPreview.start()
            } else {
                binding.saveButton.setOnClickListener {
                    viewModel.generate(requireContext(), lifecycleScope, diary)
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
