package com.klevcsoo.snapreealandroid.diary.ui.create

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.klevcsoo.snapreealandroid.SnapreealApplication
import com.klevcsoo.snapreealandroid.databinding.ActivityCreateDiaryBinding


class CreateDiaryActivity : AppCompatActivity() {
    private val viewModel: CreateDiaryViewModel by viewModels {
        CreateDiaryViewModel.Companion.CreateDiaryViewModelFactory(
            (application as SnapreealApplication).diaryRepository
        )
    }

    private lateinit var binding: ActivityCreateDiaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateDiaryBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)


        binding.createDiaryButton.setOnClickListener {
            Log.d(TAG, "Creating diary...")
            viewModel.create().invokeOnCompletion { finish() }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        focusDiaryNameInput()
    }

    private fun focusDiaryNameInput() {
        binding.diaryNameInput.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.diaryNameInput, InputMethodManager.SHOW_IMPLICIT)
    }

    companion object {
        const val TAG = "CreateDiaryActivity"
    }
}
