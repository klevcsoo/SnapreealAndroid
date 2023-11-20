package com.klevcsoo.snapreealandroid.ui.diary.details.snaps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableRow
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.klevcsoo.snapreealandroid.databinding.FragmentDiarySnapsBinding
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.util.serializable
import kotlinx.coroutines.launch


class DiarySnapsFragment : Fragment() {
    private var diary: Diary? = null

    private lateinit var viewModel: DiarySnapsViewModel
    private var _binding: FragmentDiarySnapsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            diary = it.serializable<Diary>("diary")
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

        if (diary == null) {
            Log.w(TAG, "Diary object not received")
            requireActivity().finish()
            return
        }

        viewModel = ViewModelProvider(this)[DiarySnapsViewModel::class.java]
        viewModel.load(diary!!.id)

        lifecycleScope.launch {
            viewModel.days.observe(viewLifecycleOwner) { dates ->
                val transaction = parentFragmentManager.beginTransaction()
                val rows: MutableList<TableRow> = mutableListOf()

                for (i in dates.indices step 3) {
                    val row = TableRow(context)
                    row.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    row.focusable = View.FOCUSABLE
                    row.id = 9000 + i
                    rows.add(row)

                    transaction.add(
                        row.id, SnapCardFragment
                            .newInstance(diary!!, dates[i], null)
                    )
                    if (i + 1 < dates.size) {
                        val fragment = SnapCardFragment
                            .newInstance(diary!!, dates[i + 1], null)
                        transaction.add(row.id, fragment)
                    }
                    if (i + 2 < dates.size) {
                        val fragment = SnapCardFragment
                            .newInstance(diary!!, dates[i + 2], null)
                        transaction.add(row.id, fragment)
                    }
                }

                transaction.commit()
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
