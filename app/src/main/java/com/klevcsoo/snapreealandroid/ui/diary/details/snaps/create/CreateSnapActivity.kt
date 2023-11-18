package com.klevcsoo.snapreealandroid.ui.diary.details.snaps.create

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.klevcsoo.snapreealandroid.databinding.ActivityCreateSnapBinding

class CreateSnapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateSnapBinding

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )

                val fragment = SnapCameraFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(binding.cameraFragmentContainer.id, fragment)
                    .commit()
            } else {
                Toast.makeText(
                    this,
                    "Give the app permission to use the camera to enable snap recording.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateSnapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val diaryId = intent.extras?.getString("diaryId")
        Log.d(TAG, "Diary ID: $diaryId")
        if (diaryId == null) {
            finish()
        } else {
            binding.backButton.setOnClickListener { finish() }
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    companion object {
        const val TAG = "CreateSnapActivity"
    }
}
