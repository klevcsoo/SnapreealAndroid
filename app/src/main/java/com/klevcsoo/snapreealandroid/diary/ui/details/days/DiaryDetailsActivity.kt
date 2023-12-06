package com.klevcsoo.snapreealandroid.diary.ui.details.days

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.klevcsoo.snapreealandroid.SnapreealApplication
import com.klevcsoo.snapreealandroid.databinding.ActivityDiaryDetailsBinding
import com.klevcsoo.snapreealandroid.diary.model.Diary
import com.klevcsoo.snapreealandroid.diary.ui.details.video.DiaryVideoFragment
import com.klevcsoo.snapreealandroid.snap.ui.DiarySnapsFragment
import com.klevcsoo.snapreealandroid.util.serializable

class DiaryDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryDetailsBinding
    private val viewModel by viewModels<DiaryDetailsViewModel> {
        DiaryDetailsViewModel.Companion.DiaryDetailsViewModelFactory(
            (application as SnapreealApplication).diaryRepository,
            intent.extras?.serializable<Diary>("diary")!!
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        viewModel.diary.observe(this) {
            binding.titleText.text = it.name
            binding.mainPager.adapter = DiaryPageContentAdapter(
                listOf(
                    DiarySnapsFragment.newInstance(it),
                    DiaryVideoFragment.newInstance(it)
                ), this
            )
        }
    }

    companion object {
        private class DiaryPageContentAdapter(
            private val fragments: List<Fragment>,
            fragmentActivity: FragmentActivity
        ) : FragmentStateAdapter(fragmentActivity) {
            override fun getItemCount(): Int = fragments.size

            override fun createFragment(position: Int): Fragment = fragments[position]
        }
    }
}
