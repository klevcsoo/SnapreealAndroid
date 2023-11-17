package com.klevcsoo.snapreealandroid.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.klevcsoo.snapreealandroid.util.CreatableFromSnapshot
import com.klevcsoo.snapreealandroid.util.Identifiable

data class Snap(
    override val id: String,
    val date: Timestamp,
    val isThumbnailDark: Boolean,
    val mediaLength: Long,
    val thumbnailUrl: String,
    val videoUrl: String
) : Identifiable {
    companion object : CreatableFromSnapshot<Snap> {
        override fun createFrom(snapshot: DocumentSnapshot): Snap {
            val date = snapshot.get("date", Timestamp::class.java)
            val isDark = snapshot.getBoolean("isThumbnailDark")
            val length = snapshot.getLong("mediaLength")
            val thumbnail = snapshot.getString("thumbnailUrl")
            val video = snapshot.getString("videoUrl")

            if (date == null || isDark == null || length == null ||
                thumbnail == null || video == null
            ) {
                throw Error("Incomplete Snap entity: ${snapshot.reference.path}")
            }

            return Snap(snapshot.id, date, isDark, length, thumbnail, video)
        }
    }
}
