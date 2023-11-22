package com.klevcsoo.snapreealandroid.ui.diary.list

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.klevcsoo.snapreealandroid.databinding.ActivityDiaryListBinding
import com.klevcsoo.snapreealandroid.service.FirebaseService
import com.klevcsoo.snapreealandroid.ui.auth.LoginActivity
import com.klevcsoo.snapreealandroid.ui.diary.create.CreateDiaryActivity
import com.squareup.picasso.Picasso

class DiaryListActivity : AppCompatActivity() {

    private val auth = FirebaseService.instance.auth

    private lateinit var viewModel: DiaryListViewModel

    private lateinit var binding: ActivityDiaryListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DiaryListViewModel::class.java]
        binding = ActivityDiaryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        viewModel.userPhoto.observe(this) {
            Picasso.get().load(it).into(binding.avatarImage)
        }

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
