package com.klevcsoo.snapreealandroid.snap.ui.create

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.klevcsoo.snapreealandroid.SnapreealApplication
import com.klevcsoo.snapreealandroid.databinding.ActivityCreateSnapBinding
import com.klevcsoo.snapreealandroid.diary.dto.DiaryDay
import com.klevcsoo.snapreealandroid.util.serializable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CreateSnapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateSnapBinding
    private val viewModel by viewModels<CreateSnapViewModel> {
        CreateSnapViewModel.Companion.CreateSnapViewModelFactory(
            (application as SnapreealApplication).mediaRepository,
            intent.extras!!.serializable<DiaryDay>(ARG_DIARY_DAY)!!
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateSnapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }
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

            if (viewModel.diaryDay.snap == null) {
                val fragment = SnapCameraFragment.newInstance(viewModel.diaryDay)
                supportFragmentManager.commit {
                    replace(binding.contentFragment.id, fragment)
                }
            } else {
                val context = this
                lifecycleScope.launch(Dispatchers.IO) {
                    val file = viewModel.getFile(context)
                    val fragment = SnapInspectorFragment.newInstance(viewModel.diaryDay, file)
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
        val fragment = SnapInspectorFragment.newInstance(viewModel.diaryDay, video, true)
        supportFragmentManager.commit {
            replace(binding.contentFragment.id, fragment)
        }
    }

    fun discardSnap() {
        val fragment = SnapCameraFragment.newInstance(viewModel.diaryDay)
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
