package com.klevcsoo.snapreealandroid.repository

import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.klevcsoo.snapreealandroid.model.Diary
import com.klevcsoo.snapreealandroid.model.Snap
import com.klevcsoo.snapreealandroid.service.FirebaseService
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

    private suspend fun getSnapDBRef(uid: String, diary: Diary, day: Long): DocumentReference {
        val snapCollection = firestore.collection("users").document(uid)
            .collection("diaries").document(diary.id)
            .collection("snaps")

        val snapshot = snapCollection.limit(1).whereEqualTo("day", day).get().await()
        return if (snapshot.isEmpty) snapCollection.document() else snapshot.documents[0].reference
    }

    private fun generateThumbnailFor(file: File): File {
        val outPath = "${file.absolutePath}.jpg"
        val cmd = "-i ${file.absolutePath} -vframes 1 -q:v 2 $outPath"
        val session = FFmpegKit.execute(cmd)
        if (ReturnCode.isSuccess(session.returnCode)) {
            return File(outPath)
        } else if (ReturnCode.isCancel(session.returnCode)) {
            throw Error("FFMpeg session is cancelled.")
        } else {
            throw Error("FFMpeg session exited with an error: ${session.failStackTrace}")
        }
    }

    private fun calculateIsThumbnailDark(file: File): Boolean {
        val pixelSpacing = 1
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)

        var r = 0
        var g = 0
        var b = 0
        val height = bitmap.height
        val width = bitmap.width
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var i = 0

        while (i < pixels.size) {
            val color = pixels[i]
            r += Color.red(color)
            g += Color.green(color)
            b += Color.blue(color)
            i += pixelSpacing
        }

        return (r + b + g) / (pixels.size * 3) > 80
    }

    companion object {
        const val TAG = "DiaryRepository"
    }
}
