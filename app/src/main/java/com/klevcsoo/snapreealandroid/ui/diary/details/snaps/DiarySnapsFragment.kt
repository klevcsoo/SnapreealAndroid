package com.klevcsoo.snapreealandroid.ui.diary.details.snaps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableRow
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.klevcsoo.snapreealandroid.databinding.FragmentDiarySnapsBinding
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.util.serializable


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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiarySnapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.days.observe(viewLifecycleOwner) { days ->
            binding.snapRowTable.removeAllViews()

            parentFragmentManager.commit {
                val rows: MutableList<TableRow> = mutableListOf()

                for (i in days.indices step 3) {
                    val row = TableRow(context)
                    row.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    row.focusable = View.FOCUSABLE
                    row.id = 9000 + i
                    rows.add(row)

                    add(row.id, SnapCardFragment.newInstance(days[i]))
                    if (i + 1 < days.size) {
                        add(row.id, SnapCardFragment.newInstance(days[i + 1]))
                    }
                    if (i + 2 < days.size) {
                        add(row.id, SnapCardFragment.newInstance(days[i + 2]))
                    }
                }
                rows.forEach { binding.snapRowTable.addView(it) }

            }
        }
    }

    companion object {
        @Suppress("unused")
        const val TAG = "DiarySnapsFragment"

        private const val ARG_DIARY = "diary"

        fun newInstance(diary: Diary) = DiarySnapsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_DIARY, diary)
            }
        }
    }
}
