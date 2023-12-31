package com.klevcsoo.snapreealandroid.snap.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.klevcsoo.snapreealandroid.SnapreealApplication
import com.klevcsoo.snapreealandroid.databinding.FragmentDiarySnapsBinding
import com.klevcsoo.snapreealandroid.diary.dto.DiaryDay
import com.klevcsoo.snapreealandroid.diary.model.Diary
import com.klevcsoo.snapreealandroid.media.dimmedFilter
import com.klevcsoo.snapreealandroid.snap.ui.create.CreateSnapActivity
import com.klevcsoo.snapreealandroid.util.serializable
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


class DiarySnapsFragment : Fragment() {
    private var _binding: FragmentDiarySnapsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DiarySnapsViewModel> {
        DiarySnapsViewModel.Companion.DiarySnapsViewModelFactory(
            (requireActivity().application as SnapreealApplication).snapRepository,
            requireArguments().serializable<Diary>("diary")!!,
            viewLifecycleOwner
        )
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
                        thumbnailImage.setImageURI(Uri.parse(diaryDay.snap!!.thumbnailUrl))

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
            }

            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): DaysGridViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_diary_day, parent, false)
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
