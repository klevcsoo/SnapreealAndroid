package com.klevcsoo.snapreealandroid.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.klevcsoo.snapreealandroid.databinding.ActivityDiaryListBinding
import com.klevcsoo.snapreealandroid.service.FirebaseService
import com.squareup.picasso.Picasso

class DiaryListActivity : AppCompatActivity() {

    private val auth = FirebaseService.instance.auth

    private lateinit var binding: ActivityDiaryListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            val user = auth.currentUser as FirebaseUser
            val photoUrl = user.photoUrl
            Log.d(TAG, "User photo URL: $photoUrl")
            Picasso.get().load(photoUrl).into(binding.avatarImage)
        }
    }

    companion object {
        const val TAG = "DiaryListActivity"
    }
}
