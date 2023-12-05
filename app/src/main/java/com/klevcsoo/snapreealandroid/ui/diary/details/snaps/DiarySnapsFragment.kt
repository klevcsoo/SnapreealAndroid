package com.klevcsoo.snapreealandroid.ui.diary.details.snaps

import android.content.Context
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.klevcsoo.snapreealandroid.R
import com.klevcsoo.snapreealandroid.databinding.FragmentDiarySnapsBinding
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.model.DiaryDay
import com.klevcsoo.snapreealandroid.ui.diary.details.snaps.create.CreateSnapActivity
import com.klevcsoo.snapreealandroid.util.serializable
import com.squareup.picasso.Picasso
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


class DiarySnapsFragment : Fragment() {
    private lateinit var diary: Diary

    private val viewModel by viewModels<DiarySnapsViewModel> {
        DiarySnapsViewModel.Companion.DiarySnapsViewModelFactory()
    }
    private var _binding: FragmentDiarySnapsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            diary = it.serializable<Diary>("diary")!!
            viewModel.load(diary)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiarySnapsBinding.inflate(inflater, container, false)
        binding.dayGridRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        updateRecyclerView(listOf())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.days.observe(viewLifecycleOwner) { days ->
            updateRecyclerView(days)
        }
    }

    private fun updateRecyclerView(days: List<DiaryDay>) {
        val adapter = DaysGridAdapter(days, requireContext())
        adapter.setHasStableIds(true)
        binding.dayGridRecyclerView.removeAllViews()
        binding.dayGridRecyclerView.adapter = adapter
    }

    companion object {
        @Suppress("unused")
        const val TAG = "DiarySnapsFragment"

        private const val ARG_DIARY = "diary"
        private const val ARG_DIARY_DAY = "diaryDay"

        fun newInstance(diary: Diary) = DiarySnapsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_DIARY, diary)
            }
        }

        private class DaysGridAdapter(
            private val days: List<DiaryDay>, private val context: Context
        ) : RecyclerView.Adapter<DaysGridAdapter.DaysGridViewHolder>() {

            class DaysGridViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                val thumbnailImage: ImageView = view.findViewById(R.id.diaryDayThumbnailImage)
                val dayText: TextView = view.findViewById(R.id.diaryDayDayText)
                val dateText: TextView = view.findViewById(R.id.diaryDayDateText)

                fun bind(context: Context, diaryDay: DiaryDay) {
                    val date = LocalDate.ofEpochDay(diaryDay.day)
                    if (date.dayOfMonth == 1) {
                        dateText.text = date.month.getDisplayName(
                            TextStyle.SHORT, Locale.getDefault()
                        )
                        dayText.text = ""
                    } else {
                        dateText.text = date.dayOfMonth.toString()
                        dayText.text = date.dayOfWeek.getDisplayName(
                            TextStyle.SHORT, Locale.getDefault()
                        )
                    }

                    if (date.toEpochDay() == LocalDate.now().toEpochDay()) {
                        itemView.setBackgroundColor(
                            ContextCompat.getColor(context, R.color.primary_300)
                        )
                    }

                    if (diaryDay.snap != null) {
                        Log.d(TAG, "day: ${diaryDay.day}, snap: ${diaryDay.snap!!.day}")
                        Picasso.get().load(diaryDay.snap!!.thumbnailUrl).into(thumbnailImage)

                        if (diaryDay.snap!!.isThumbnailDark) {
                            dateText.setTextColor(ContextCompat.getColor(context, R.color.white))
                            dayText.setTextColor(ContextCompat.getColor(context, R.color.white))
                            thumbnailImage.colorFilter = dimmedFilter()
                        }
                    }

                    itemView.setOnClickListener {
                        Log.d(TAG, "opening day ${diaryDay.day}")
                        val intent = Intent(context, CreateSnapActivity::class.java)
                        intent.putExtra(ARG_DIARY_DAY, diaryDay)
                        context.startActivity(intent)
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
            }

            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): DaysGridViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.diary_day_card, parent, false)
                return DaysGridViewHolder(view)
            }

            override fun onBindViewHolder(holder: DaysGridViewHolder, position: Int) {
                holder.setIsRecyclable(false)
                holder.bind(context, days[position])
            }

            override fun getItemCount() = days.size
        }
    }
}
