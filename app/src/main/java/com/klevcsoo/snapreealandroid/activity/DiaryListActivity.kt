package com.klevcsoo.snapreealandroid.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.klevcsoo.snapreealandroid.databinding.ActivityDiaryListBinding
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.model.view.DiaryListViewModel
import com.klevcsoo.snapreealandroid.service.FirebaseService
import com.squareup.picasso.Picasso

class DiaryListActivity : AppCompatActivity() {

    private val auth = FirebaseService.instance.auth

    private lateinit var viewModel: DiaryListViewModel

    private lateinit var binding: ActivityDiaryListBinding

    private var diaryList: List<Diary> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DiaryListViewModel::class.java]
        binding = ActivityDiaryListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            Picasso.get().load(viewModel.getPhotoURL()).into(binding.avatarImage)
            viewModel.onList { diaries ->
                diaryList = diaries
                Log.d(TAG, "Diaries: $diaryList")
            }
        }

        binding.createDiaryButton.setOnClickListener {
            startActivity(Intent(this, CreateDiaryActivity::class.java))
        }
    }

    companion object {
        const val TAG = "DiaryListActivity"
    }
}
