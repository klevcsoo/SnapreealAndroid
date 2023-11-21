package com.klevcsoo.snapreealandroid.ui.diary.details.snaps.create

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.klevcsoo.snapreealandroid.databinding.ActivityCreateSnapBinding
import com.klevcsoo.snapreealandroid.model.DiaryDay
import com.klevcsoo.snapreealandroid.repository.MediaRepository
import com.klevcsoo.snapreealandroid.util.serializable
import java.io.File

class CreateSnapActivity : AppCompatActivity() {
    private val mediaRepository = MediaRepository()

    private lateinit var binding: ActivityCreateSnapBinding

    private lateinit var diaryDay: DiaryDay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateSnapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        intent.extras?.let {
            diaryDay = it.serializable<DiaryDay>(ARG_DIARY_DAY)!!

            requestRequiredPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQ_PERMISSIONS_RES_CODE) {
            if (grantResults.any { it == PackageManager.PERMISSION_DENIED }) {
                val content = permissions.mapIndexed { index, p -> "$p: ${grantResults[index]}" }
                    .joinToString("\n")
                Log.w(TAG, "Not all permissions granted: $content")
                return
            }

            Log.d(TAG, "All permissions in order, showing camera fragment")
            makeActivityFullscreen()

            if (diaryDay.snap == null) {
                val fragment = SnapCameraFragment.newInstance(diaryDay)
                supportFragmentManager.commit {
                    replace(binding.contentFragment.id, fragment)
                }
            } else {
                mediaRepository.getSnapVideoFile(this, diaryDay) {
                    val fragment = SnapInspectorFragment.newInstance(diaryDay, it)
                    supportFragmentManager.commit {
                        replace(binding.contentFragment.id, fragment)
                    }
                }
            }
        }
    }

    fun requestRequiredPermissions() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ), REQ_PERMISSIONS_RES_CODE
        )
    }

    fun inspectSnap(video: File) {
        val fragment = SnapInspectorFragment.newInstance(diaryDay, video, true)
        supportFragmentManager.commit {
            replace(binding.contentFragment.id, fragment)
        }
    }

    fun discardSnap() {
        val fragment = SnapCameraFragment.newInstance(diaryDay)
        supportFragmentManager.commit {
            replace(binding.contentFragment.id, fragment)
        }
    }

    private fun makeActivityFullscreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            val decorView = window.decorView
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility =
                decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }

    companion object {
        const val TAG = "CreateSnapActivity"

        private const val REQ_PERMISSIONS_RES_CODE = 36375
        private const val ARG_DIARY_DAY = "diaryDay"
    }
}
