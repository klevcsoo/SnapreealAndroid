package com.klevcsoo.snapreealandroid.service.impl

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.klevcsoo.snapreealandroid.BuildConfig
import com.klevcsoo.snapreealandroid.service.FirebaseServiceInstance

class FirebaseServiceInstanceImpl : FirebaseServiceInstance {
    override val auth: FirebaseAuth
        get() = Firebase.auth
    override val functions: FirebaseFunctions
        get() = Firebase.functions
    override val firestore: FirebaseFirestore
        get() = Firebase.firestore
    override val storage: FirebaseStorage
        get() = Firebase.storage

    init {
        Log.d(TAG, "Application build type: ${BuildConfig.BUILD_TYPE}")
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Running in DEBUG mode (using Firebase Emulators)")

            val emulatorsHost = "localhost"
            auth.useEmulator(emulatorsHost, 9099)
            functions.useEmulator(emulatorsHost, 5001)
            firestore.useEmulator(emulatorsHost, 8080)
            storage.useEmulator(emulatorsHost, 9199)
        }
    }

    companion object {
        const val TAG = "FirebaseServiceInstance"
    }
}
