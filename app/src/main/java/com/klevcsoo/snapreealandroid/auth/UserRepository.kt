package com.klevcsoo.snapreealandroid.auth

import android.net.Uri
import android.util.Log
import com.klevcsoo.snapreealandroid.service.FirebaseService

class UserRepository {
    private val auth = FirebaseService.instance.auth

    fun getUserPhotoURL(): Uri? {
        if (auth.currentUser == null) {
            Log.w(TAG, "Cannot load user photo URL: user is unauthenticated")
            return null
        }

        val photoUrl = auth.currentUser!!.photoUrl
        val highRes = photoUrl.toString().replace("s96-c", "s400-c")

        return Uri.parse(highRes)
    }

    companion object {
        const val TAG = "UserRepository"
    }
}
