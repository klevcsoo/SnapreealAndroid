package com.klevcsoo.snapreealandroid.activity

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.klevcsoo.snapreealandroid.databinding.ActivityCreateDiaryBinding
import com.klevcsoo.snapreealandroid.model.view.CreateDiaryViewModel


class CreateDiaryActivity : AppCompatActivity() {
    private lateinit var viewModel: CreateDiaryViewModel

    private lateinit var binding: ActivityCreateDiaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CreateDiaryViewModel::class.java]
        binding = ActivityCreateDiaryBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)


        binding.createDiaryButton.setOnClickListener {
            Log.d(TAG, "Creating diary...")
            viewModel.create { finish() }
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
