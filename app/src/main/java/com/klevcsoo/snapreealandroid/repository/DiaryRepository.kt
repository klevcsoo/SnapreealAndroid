package com.klevcsoo.snapreealandroid.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.service.FirebaseService
import kotlinx.coroutines.tasks.await
import java.util.Date

class DiaryRepository {
    private val auth = FirebaseService.instance.auth
    private val firestore = FirebaseService.instance.firestore

    fun onDiaryList(listener: (diaries: List<Diary>) -> Unit) {
        if (auth.currentUser === null) {
            Log.w(TAG, "Cannot load diary list: user is unauthenticated")
            return
        }

        val ref = firestore.collection("users").document(auth.currentUser!!.uid)
            .collection("diaries")
        ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Failed to load diaries", error)
                return@addSnapshotListener
            }

            try {
                listener(snapshot!!.map { diarySnapshot -> Diary.createFrom(diarySnapshot) })
            } catch (e: Error) {
                Log.w(TAG, "${e.message}")
            }
        }
    }

    suspend fun createDiary(name: String): DocumentReference {
        if (auth.currentUser === null) {
            throw Error("Cannot create diary: user is unauthenticated")
        }

        val data = hashMapOf(
            "name" to name,
            "createdAt" to Timestamp(Date())
        )

        return firestore.collection("users").document(auth.currentUser!!.uid)
            .collection("diaries").add(data).await()
    }

    companion object {
        const val TAG = "DiaryRepository"
    }
}
