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
import com.klevcsoo.snapreealandroid.R
import com.klevcsoo.snapreealandroid.databinding.ActivityCreateSnapBinding
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.util.serializable
import java.io.File
import kotlin.properties.Delegates

class CreateSnapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateSnapBinding

    private lateinit var diary: Diary
    private var snapDay by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateSnapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        intent.extras?.let {
            diary = it.serializable<Diary>(ARG_DIARY) ?: return
            snapDay = it.getLong(ARG_DAY)

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

            val fragment = SnapCameraFragment.newInstance(diary, snapDay)
            supportFragmentManager.commit {
                replace(binding.contentFragment.id, fragment)
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

    fun inspectSnap(snapFile: File) {
        val fragment = SnapInspectorFragment.newInstance(diary, snapDay, snapFile)
        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out
            )
            replace(binding.contentFragment.id, fragment)
        }
    }

    fun discardSnap() {
        val fragment = SnapCameraFragment.newInstance(diary, snapDay)
        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_out, R.anim.fade_out, R.anim.fade_in, R.anim.slide_in
            )
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

        private const val ARG_DIARY = "diary"
        private const val ARG_DAY = "snapDay"
    }
}
