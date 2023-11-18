package com.klevcsoo.snapreealandroid.ui.diary.details.snaps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.klevcsoo.snapreealandroid.databinding.FragmentDiarySnapsBinding
import com.klevcsoo.snapreealandroid.model.Diary
import kotlinx.coroutines.launch

class DiarySnapsFragment : Fragment() {
    private var diaryId: String? = null

    private lateinit var viewModel: DiarySnapsViewModel
    private var _binding: FragmentDiarySnapsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            diaryId = it.getString("diaryId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiarySnapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DiarySnapsViewModel::class.java]
        viewModel.load(diaryId!!)

        lifecycleScope.launch {
            viewModel.dates.observe(viewLifecycleOwner) { dates ->
                val transaction = parentFragmentManager.beginTransaction()

                for (i in dates.indices step 3) {
                    val row = LinearLayout(context)
                    row.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    row.orientation = LinearLayout.HORIZONTAL
                    row.id = 9000 + i
                    binding.snapRowList.addView(row)

                    transaction.add(row.id, SnapCardFragment.newInstance(dates[i], null))
                    if (i + 1 < dates.size) {
                        transaction.add(
                            row.id,
                            SnapCardFragment.newInstance(dates[i + 1], null)
                        )
                    }
                    if (i + 2 < dates.size) {
                        transaction.add(
                            row.id,
                            SnapCardFragment.newInstance(dates[i + 2], null)
                        )
                    }
                }

                transaction.commit()
            }
        }.invokeOnCompletion {
            binding.root.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    companion object {
        @Suppress("unused")
        const val TAG = "DiarySnapsFragment"

        fun newInstance(diary: Diary) = DiarySnapsFragment().apply {
            arguments = Bundle().apply {
                putString("diaryId", diary.id)
            }
        }
    }
}
