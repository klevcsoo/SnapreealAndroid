package com.klevcsoo.snapreealandroid.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage

interface FirebaseServiceInstance {
    val auth: FirebaseAuth
    val functions: FirebaseFunctions
    val firestore: FirebaseFirestore
    val storage: FirebaseStorage
}
