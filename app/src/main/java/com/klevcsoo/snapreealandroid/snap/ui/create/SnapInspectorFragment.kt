package com.klevcsoo.snapreealandroid.snap.ui.create

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.klevcsoo.snapreealandroid.R
import com.klevcsoo.snapreealandroid.SnapreealApplication
import com.klevcsoo.snapreealandroid.databinding.FragmentSnapInspectorBinding
import com.klevcsoo.snapreealandroid.diary.dto.DiaryDay
import com.klevcsoo.snapreealandroid.util.serializable
import kotlinx.coroutines.launch
import java.io.File

class SnapInspectorFragment : Fragment() {
    private var _binding: FragmentSnapInspectorBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SnapInspectorViewModel> {
        requireArguments().let {
            SnapInspectorViewModel.Companion.SnapInspectorViewModelFactory(
                (requireActivity().application as SnapreealApplication).snapRepository,
                it.serializable<DiaryDay>(ARG_DIARY_DAY)!!,
                it.serializable<File>(ARG_VIDEO)!!,
                it.getBoolean(ARG_IGNORE_SNAP)
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSnapInspectorBinding.inflate(inflater, container, false)

        if (viewModel.diaryDay.snap != null && !viewModel.ignoreSnap) {
            binding.uploadButton.text = "replace snap"
            binding.uploadButton.contentDescription = "replace snap"
            binding.uploadButton.icon = AppCompatResources.getDrawable(
                requireContext(), R.drawable.rounded_autorenew_24
            )
        }

        binding.uploadButton.setOnClickListener {
            if (viewModel.diaryDay.snap != null && !viewModel.ignoreSnap) {
                (requireActivity() as CreateSnapActivity).discardSnap()
            } else {
                upload()
            }
        }
        binding.discardButton.setOnClickListener {
            if (viewModel.diaryDay.snap != null && !viewModel.ignoreSnap) {
                lifecycleScope.launch {
                    viewModel.deleteSnap()
                    requireActivity().finish()
                }
            } else {
                (requireActivity() as CreateSnapActivity).discardSnap()
            }
        }

        binding.mediaPreview.setVideoPath(viewModel.snapFile.path)
        binding.mediaPreview.setOnPreparedListener { it.isLooping = true }
        binding.mediaPreview.start()

        return binding.root
    }

    private fun upload() {
        binding.uploadButton.isEnabled = false
        binding.discardButton.isEnabled = false
        requireActivity().lifecycleScope.launch {
            viewModel.uploadSnap()
        }.invokeOnCompletion {
            if (it != null) {
                Log.w(TAG, "Failed to upload snap", it)
                Toast.makeText(requireContext(), "Could not upload snap", Toast.LENGTH_LONG)
                    .show()
            }

            binding.uploadButton.isEnabled = true
            binding.discardButton.isEnabled = true

            requireActivity().finish()
        }
    }

    companion object {
        const val TAG = "SnapInspectorFragment"

        private const val ARG_DIARY_DAY = "diaryDay"
        private const val ARG_VIDEO = "video"
        private const val ARG_IGNORE_SNAP = "ignoreSnap"

        @JvmStatic
        fun newInstance(day: DiaryDay, video: File, ignoreSnap: Boolean) =
            SnapInspectorFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DIARY_DAY, day)
                    putSerializable(ARG_VIDEO, video)
                    putBoolean(ARG_IGNORE_SNAP, ignoreSnap)
                }
            }

        fun newInstance(day: DiaryDay, video: File) = newInstance(day, video, false)
    }
}
