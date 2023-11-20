package com.klevcsoo.snapreealandroid.ui.diary.details.snaps.create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.klevcsoo.snapreealandroid.databinding.FragmentSnapInspectorBinding
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.repository.DiaryRepository
import com.klevcsoo.snapreealandroid.util.serializable
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

class SnapInspectorFragment : Fragment() {
    private var _binding: FragmentSnapInspectorBinding? = null
    private val binding get() = _binding!!

    private val repository = DiaryRepository()

    private lateinit var diary: Diary
    private lateinit var snapDate: LocalDate
    private lateinit var snapFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            diary = it.serializable<Diary>(ARG_DIARY)!!
            snapDate = it.serializable<LocalDate>(ARG_SNAP_DATE)!!
            snapFile = it.serializable<File>(ARG_SNAP_FILE)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSnapInspectorBinding.inflate(inflater, container, false)

        binding.uploadButton.setOnClickListener { upload() }
        binding.discardButton.setOnClickListener {
            (requireActivity() as CreateSnapActivity).discardSnap()
        }

        binding.mediaPreview.setVideoPath(snapFile.path)
        binding.mediaPreview.setOnPreparedListener { it.isLooping = true }
        binding.mediaPreview.start()

        return binding.root
    }

    private fun upload() {
        binding.uploadButton.isEnabled = false
        binding.discardButton.isEnabled = false
        requireActivity().lifecycleScope.launch {
            repository.uploadSnap(diary, snapDate, snapFile)
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

        private const val ARG_DIARY = "diary"
        private const val ARG_SNAP_DATE = "snapDate"
        private const val ARG_SNAP_FILE = "snapFile"

        @JvmStatic
        fun newInstance(diary: Diary, snapDate: LocalDate, snapFile: File) =
            SnapInspectorFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DIARY, diary)
                    putSerializable(ARG_SNAP_DATE, snapDate)
                    putSerializable(ARG_SNAP_FILE, snapFile)
                }
            }
    }
}
