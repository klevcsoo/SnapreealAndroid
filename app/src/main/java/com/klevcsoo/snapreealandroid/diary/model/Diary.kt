package com.klevcsoo.snapreealandroid.diary.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.klevcsoo.snapreealandroid.util.CreatableFromSnapshot
import com.klevcsoo.snapreealandroid.util.Identifiable
import java.io.Serializable
import java.util.Date

data class Diary(
    override val id: String,
    val createdAt: Date,
    val name: String
) : Identifiable, Serializable {
    companion object : CreatableFromSnapshot<Diary> {
        override fun createFrom(snapshot: DocumentSnapshot): Diary {
            val createdAt = snapshot.get("createdAt", Timestamp::class.java)
            val name = snapshot.getString("name")

            if (createdAt == null || name == null) {
                throw Error("Incomplete Diary entity: ${snapshot.reference.path}")
            }

            return Diary(snapshot.id, createdAt.toDate(), name)
        }
    }
}
