package com.klevcsoo.snapreealandroid.ui.diary.details.snaps.create

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.klevcsoo.snapreealandroid.databinding.ActivityCreateSnapBinding
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.util.serializable
import java.time.LocalDate

class CreateSnapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateSnapBinding

    private lateinit var diary: Diary
    private lateinit var snapDate: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateSnapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        intent.extras?.let {
            diary = it.serializable<Diary>(ARG_DIARY) ?: return
            snapDate = it.serializable<LocalDate>(ARG_DATE) ?: return

            requestPermissions(
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ), REQ_PERMISSIONS_RES_CODE
            )
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

            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )

            val fragment = SnapCameraFragment.newInstance(diary, snapDate)
            supportFragmentManager.beginTransaction()
                .replace(binding.cameraFragmentContainer.id, fragment)
                .commit()
        }
    }

    companion object {
        const val TAG = "CreateSnapActivity"

        private const val REQ_PERMISSIONS_RES_CODE = 36375

        private const val ARG_DIARY = "diary"
        private const val ARG_DATE = "snapDate"
    }
}
