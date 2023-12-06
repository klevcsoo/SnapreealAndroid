package com.klevcsoo.snapreealandroid

import android.app.Application
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.functions.functions
import com.google.firebase.storage.storage
import com.klevcsoo.snapreealandroid.diary.DiaryRepository
import com.klevcsoo.snapreealandroid.media.MediaRepository
import com.klevcsoo.snapreealandroid.snap.SnapRepository

class SnapreealApplication : Application() {

    private val database by lazy { SnapreealDatabase.get(this) }
    val diaryRepository by lazy { DiaryRepository(database.diaryDao()) }
    val snapRepository by lazy { SnapRepository(database.snapDao()) }
    val mediaRepository by lazy { MediaRepository() }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, BuildConfig.BUILD_TYPE)
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Running in DEBUG mode (using Firebase Emulators)")

            val emulatorsHost = "localhost"
            Firebase.auth.useEmulator(emulatorsHost, 9099)
            Firebase.functions.useEmulator(emulatorsHost, 5001)
            Firebase.firestore.useEmulator(emulatorsHost, 8080)
            Firebase.storage.useEmulator(emulatorsHost, 9199)
        }
    }

    companion object {
        private const val TAG = "SnapreealApplication"
    }
}
