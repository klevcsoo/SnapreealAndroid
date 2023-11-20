package com.klevcsoo.snapreealandroid.ui.diary.details.snaps

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.klevcsoo.snapreealandroid.databinding.FragmentSnapCardBinding
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.model.Snap
import com.klevcsoo.snapreealandroid.ui.diary.details.snaps.create.CreateSnapActivity
import com.klevcsoo.snapreealandroid.util.serializable
import java.time.LocalDate
import java.time.format.TextStyle
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

        arguments?.let { bundle ->
            val day = bundle.getLong(ARG_DAY)
            val date = LocalDate.ofEpochDay(day)

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

            binding.root.setOnClickListener {
                val intent = Intent(context, CreateSnapActivity::class.java)
                intent.putExtra(ARG_DIARY, bundle.serializable<Diary>(ARG_DIARY))
                intent.putExtra(ARG_DAY, date)
                startActivity(intent)
            }
        }
    }

    companion object {
        private const val ARG_DIARY = "diary"
        private const val ARG_DAY = "snapDay"
        private const val ARG_SNAP = "snap"

        @Suppress("unused")
        const val TAG = "SnapCardFragment"

        fun newInstance(diary: Diary, day: Long, snap: Snap?) = SnapCardFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_DIARY, diary)
                putSerializable(ARG_SNAP, snap)
                putLong(ARG_DAY, day)
            }
        }
    }
}
