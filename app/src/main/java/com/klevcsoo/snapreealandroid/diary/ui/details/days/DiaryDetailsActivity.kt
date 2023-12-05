package com.klevcsoo.snapreealandroid.diary.ui.details.days

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.klevcsoo.snapreealandroid.databinding.ActivityDiaryDetailsBinding
import com.klevcsoo.snapreealandroid.diary.model.Diary
import com.klevcsoo.snapreealandroid.diary.ui.details.video.DiaryVideoFragment
import com.klevcsoo.snapreealandroid.snap.ui.DiarySnapsFragment
import com.klevcsoo.snapreealandroid.util.serializable

class DiaryDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryDetailsBinding
    private lateinit var viewModel: DiaryDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DiaryDetailsViewModel::class.java]
        binding = ActivityDiaryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        val diary = intent.extras?.serializable<Diary>("diary")
        if (diary == null) {
            finish()
        } else {
            viewModel.load(diary.id)
            viewModel.diary.observe(this) {
                binding.titleText.text = it.name
                binding.mainPager.adapter = DiaryPageContentAdapter(
                    listOf(
                        DiarySnapsFragment.newInstance(it),
                        DiaryVideoFragment.newInstance(diary)
                    ), this
                )
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
