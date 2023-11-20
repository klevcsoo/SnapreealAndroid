package com.klevcsoo.snapreealandroid.ui.diary.details.snaps

import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.klevcsoo.snapreealandroid.R
import com.klevcsoo.snapreealandroid.databinding.FragmentSnapCardBinding
import com.klevcsoo.snapreealandroid.model.DiaryDay
import com.klevcsoo.snapreealandroid.ui.diary.details.snaps.create.CreateSnapActivity
import com.klevcsoo.snapreealandroid.util.serializable
import com.squareup.picasso.Picasso
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
            val diaryDay = bundle.serializable<DiaryDay>(ARG_DIARY_DAY)!!

            val date = LocalDate.ofEpochDay(diaryDay.day)
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

            if (diaryDay.snap != null) {
                Picasso.get().load(diaryDay.snap!!.thumbnailUrl).into(binding.thumbnailImage)

                if (diaryDay.snap!!.isThumbnailDark) {
                    binding.dateText.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.white)
                    )
                    binding.dayText.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.white)
                    )
                    binding.thumbnailImage.colorFilter = dimmedFilter()
                }
            }

            binding.root.setOnClickListener {
                val intent = Intent(context, CreateSnapActivity::class.java)
                intent.putExtra(ARG_DIARY_DAY, diaryDay)
                startActivity(intent)
            }
        }
    }

    private fun dimmedFilter(): ColorMatrixColorFilter {
        val fb = -50F
        val cmB = ColorMatrix()
        cmB.set(
            floatArrayOf(
                1f, 0f, 0f, 0f, fb,
                0f, 1f, 0f, 0f, fb,
                0f, 0f, 1f, 0f, fb,
                0f, 0f, 0f, 1f, 0f
            )
        )
        val colorMatrix = ColorMatrix()
        colorMatrix.set(cmB)
        return ColorMatrixColorFilter(colorMatrix)
    }


    companion object {
        private const val ARG_DIARY_DAY = "diaryDay"

        @Suppress("unused")
        const val TAG = "SnapCardFragment"

        fun newInstance(day: DiaryDay) = SnapCardFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_DIARY_DAY, day)
            }
        }
    }
}
