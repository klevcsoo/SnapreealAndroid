package com.klevcsoo.snapreealandroid.snap.model

import com.google.firebase.firestore.DocumentSnapshot
import com.klevcsoo.snapreealandroid.util.CreatableFromSnapshot
import com.klevcsoo.snapreealandroid.util.Identifiable
import java.io.Serializable

data class Snap(
    override val id: String,
    val day: Long,
    val isThumbnailDark: Boolean,
    val mediaLength: Long,
    val thumbnailUrl: String,
    val videoUrl: String
) : Identifiable, Serializable {
    companion object : CreatableFromSnapshot<Snap> {
        override fun createFrom(snapshot: DocumentSnapshot): Snap {
            val day = snapshot.getLong("day")
            val isDark = snapshot.getBoolean("isThumbnailDark")
            val length = snapshot.getLong("mediaLength")
            val thumbnail = snapshot.getString("thumbnailUrl")
            val video = snapshot.getString("videoUrl")

            if (day == null || isDark == null || length == null ||
                thumbnail == null || video == null
            ) {
                throw Error("Incomplete Snap entity: ${snapshot.reference.path}")
            }

            return Snap(snapshot.id, day, isDark, length, thumbnail, video)
        }
    }
}
