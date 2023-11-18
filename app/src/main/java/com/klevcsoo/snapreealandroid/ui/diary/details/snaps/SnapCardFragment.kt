package com.klevcsoo.snapreealandroid.ui.diary.details.snaps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.klevcsoo.snapreealandroid.databinding.FragmentSnapCardBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

class SnapCardFragment : Fragment() {
    private var _binding: FragmentSnapCardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSnapCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val date = Instant.ofEpochMilli(it.getLong(ARG_SNAP_DATE))
                .atZone(ZoneId.systemDefault()).toLocalDate()

            if (date.dayOfMonth == 1) {
                binding.dateText.text = date.month.getDisplayName(
                    TextStyle.SHORT, Locale.getDefault()
                )
                binding.dayText.text = ""
            } else {
                binding.dateText.text = date.dayOfMonth.toString()
                binding.dayText.text = date.dayOfWeek.getDisplayName(
                    TextStyle.SHORT, Locale.getDefault()
                )
            }

        }
    }

    companion object {
        private const val ARG_SNAP_ID = "snapId"
        private const val ARG_SNAP_DATE = "snapDate"

        @Suppress("unused")
        const val TAG = "SnapCardFragment"

        fun newInstance(date: Date, snapId: String?) = SnapCardFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_SNAP_ID, snapId)
                putLong(ARG_SNAP_DATE, date.time)
            }
        }
    }
}
