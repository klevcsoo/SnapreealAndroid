package com.klevcsoo.snapreealandroid.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.model.DiaryDay
import com.klevcsoo.snapreealandroid.model.Snap
import com.klevcsoo.snapreealandroid.service.FirebaseService
import com.klevcsoo.snapreealandroid.util.calculateIsThumbnailDark
import com.klevcsoo.snapreealandroid.util.generateThumbnailFor
import kotlinx.coroutines.tasks.await
import java.io.File
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
                val diaries = snapshot!!.map { Diary.createFrom(it) }
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

    suspend fun getDiarySnapList(diary: Diary): List<Snap> {
        Log.d(TAG, "Fetching diary snaps...")

        if (auth.currentUser === null) {
            throw Error("Cannot load diary snaps: user is unauthenticated")
        }

        val snapshot = firestore.collection("users").document(auth.currentUser!!.uid)
            .collection("diaries").document(diary.id)
            .collection("snaps")
            .orderBy("day", Query.Direction.ASCENDING)
            .get().await()

        return snapshot.documents.map { Snap.createFrom(it) }
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
                val snaps = snapshot!!.map { Snap.createFrom(it) }
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

    suspend fun uploadSnap(diary: Diary, day: Long, videoFile: File) {
        if (auth.currentUser === null) {
            throw Error("Cannot create diary: user is unauthenticated")
        }

        val uid = auth.currentUser!!.uid

        Log.d(TAG, "Generating snap thumbnail...")
        val thumbnailFile = generateThumbnailFor(videoFile)
        val isDark = calculateIsThumbnailDark(thumbnailFile)

        val snapDBRef = getSnapDBRef(uid, diary, day)
        val snapStorageRef = storage.reference
            .child("media/$uid/snaps/${diary.id}/${snapDBRef.id}")

        Log.d(TAG, "Uploading video...")
        val videoUri = Uri.fromFile(videoFile)
        val videoSnapshot = snapStorageRef.child(videoFile.name)
            .putFile(videoUri).await()
        val videoPublicUrl = videoSnapshot.storage.downloadUrl.await().toString()

        Log.d(TAG, "Uploading thumbnail...")
        val thumbnailUri = Uri.fromFile(thumbnailFile)
        val thumbnailSnapshot = snapStorageRef.child(thumbnailFile.name)
            .putFile(thumbnailUri).await()
        val thumbnailPublicUrl = thumbnailSnapshot.storage.downloadUrl.await().toString()

        snapDBRef.set(
            hashMapOf(
                "day" to day,
                "mediaLength" to 3,
                "isThumbnailDark" to isDark,
                "videoUrl" to videoPublicUrl,
                "thumbnailUrl" to thumbnailPublicUrl
            )
        ).await()

        Log.d(TAG, "Snap added!")
    }

    suspend fun deleteSnap(diaryDay: DiaryDay) {
        if (auth.currentUser === null) {
            throw Error("Cannot create diary: user is unauthenticated")
        }

        val uid = auth.currentUser!!.uid

        val storageRef = storage.reference
            .child("media/$uid/snaps/${diaryDay.diary.id}/${diaryDay.snap!!.id}")
        storageRef.child("${diaryDay.day}.mp4").delete().await()
        storageRef.child("${diaryDay.day}.mp4.jpg").delete().await()

        firestore.collection("users").document(uid)
            .collection("diaries").document(diaryDay.diary.id)
            .collection("snaps").document(diaryDay.snap!!.id)
            .delete().await()
    }

    private suspend fun getSnapDBRef(uid: String, diary: Diary, day: Long): DocumentReference {
        val snapCollection = firestore.collection("users").document(uid)
            .collection("diaries").document(diary.id)
            .collection("snaps")

        val snapshot = snapCollection.limit(1).whereEqualTo("day", day).get().await()
        return if (snapshot.isEmpty) snapCollection.document() else snapshot.documents[0].reference
    }

    companion object {
        const val TAG = "DiaryRepository"
    }
}
