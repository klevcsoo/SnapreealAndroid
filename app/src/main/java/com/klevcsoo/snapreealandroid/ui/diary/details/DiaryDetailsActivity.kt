package com.klevcsoo.snapreealandroid.ui.diary.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.klevcsoo.snapreealandroid.databinding.ActivityDiaryDetailsBinding

class DiaryDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryDetailsBinding
    private lateinit var viewModel: DiaryDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DiaryDetailsViewModel::class.java]
        binding = ActivityDiaryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }
        binding.mainPager.adapter = DiaryPageContentAdapter(
            listOf(
                DiarySnapsFragment.newInstance()
            ), this
        )

        val diaryId = intent.extras?.getString("diaryId")
        if (diaryId == null) {
            finish()
        } else {
            viewModel.load(diaryId)
            viewModel.diary.observe(this) {
                binding.titleText.text = it.name
            }
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
