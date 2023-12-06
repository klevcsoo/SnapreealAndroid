package com.klevcsoo.snapreealandroid.diary.ui.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.klevcsoo.snapreealandroid.SnapreealApplication
import com.klevcsoo.snapreealandroid.databinding.ActivityDiaryListBinding
import com.klevcsoo.snapreealandroid.diary.ui.create.CreateDiaryActivity

class DiaryListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiaryListBinding
    private val viewModel by viewModels<DiaryListViewModel> {
        DiaryListViewModel.Companion.DiaryListViewModelFactory(
            (application as SnapreealApplication).diaryRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.diaries.observe(this) {
            val fragments =
                it.map { diary -> DiaryCardFragment.newInstance(diary) }
            binding.diaryPager.adapter = DiaryListAdapter(fragments, this)
        }

        binding.createDiaryButton.setOnClickListener {
            startActivity(Intent(this, CreateDiaryActivity::class.java))
        }
    }

    companion object {
        private class DiaryListAdapter(
            private val fragments: List<Fragment>,
            fragmentActivity: FragmentActivity
        ) : FragmentStateAdapter(fragmentActivity) {
            override fun getItemCount(): Int = fragments.size

            override fun createFragment(position: Int): Fragment = fragments[position]
        }
    }
}
