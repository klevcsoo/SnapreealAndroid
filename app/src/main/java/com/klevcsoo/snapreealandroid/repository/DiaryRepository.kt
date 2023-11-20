package com.klevcsoo.snapreealandroid.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.model.Snap
import com.klevcsoo.snapreealandroid.service.FirebaseService
import kotlinx.coroutines.tasks.await
import java.io.File
import java.time.LocalDate
import java.util.Date

class DiaryRepository {
    private val auth = FirebaseService.instance.auth
    private val firestore = FirebaseService.instance.firestore
    private val storage = FirebaseService.instance.storage

    fun onDiaryList(listener: (diaries: List<Diary>) -> Unit) {
        Log.d(TAG, "Fetching diaries...")

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
                val diaries = snapshot!!.map { diarySnapshot -> Diary.createFrom(diarySnapshot) }
                Log.d(TAG, "${diaries.size} diaries found")
                listener(diaries)
            } catch (e: Error) {
                Log.w(TAG, "${e.message}")
                listener(listOf())
            }
        }
    }

    fun onDiaryDetails(id: String, listener: (diary: Diary?) -> Unit) {
        Log.d(TAG, "Fetching diary details...")

        if (auth.currentUser === null) {
            Log.w(TAG, "Cannot load diary details: user is unauthenticated")
            return
        }

        val ref = firestore.collection("users").document(auth.currentUser!!.uid)
            .collection("diaries").document(id)
        ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Failed to load diaries", error)
                return@addSnapshotListener
            }

            try {
                val diary = Diary.createFrom(snapshot!!)
                Log.d(TAG, "${diary.id} fetched")
                listener(diary)
            } catch (e: Error) {
                Log.w(TAG, "${e.message}")
                listener(null)
            }
        }
    }

    fun onDiarySnapList(id: String, listener: (snaps: List<Snap>) -> Unit) {
        Log.d(TAG, "Fetching diary snaps...")

        if (auth.currentUser === null) {
            Log.w(TAG, "Cannot load diary snaps: user is unauthenticated")
            return
        }

        val ref = firestore.collection("users").document(auth.currentUser!!.uid)
            .collection("diaries").document(id)
            .collection("snaps")
        ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Failed to load diaries", error)
                return@addSnapshotListener
            }

            try {
                val snaps = snapshot!!.map { snapSnapshot -> Snap.createFrom(snapSnapshot) }
                Log.d(TAG, "${snaps.size} snaps found in diary $id")
                listener(snaps)
            } catch (e: Error) {
                Log.w(TAG, "${e.message}")
                listener(listOf())
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

    suspend fun uploadSnap(diary: Diary, date: LocalDate, file: File) {
        if (auth.currentUser === null) {
            throw Error("Cannot create diary: user is unauthenticated")
        }

        val uid = auth.currentUser!!.uid

        TODO("Not yet implemented")
    }

    companion object {
        const val TAG = "DiaryRepository"
    }
}
