package com.klevcsoo.snapreealandroid.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.klevcsoo.snapreealandroid.util.CreatableFromSnapshot
import com.klevcsoo.snapreealandroid.util.Identifiable

data class Diary(
    override val id: String,
    val createdOn: Timestamp,
    val name: String
) : Identifiable {
    companion object : CreatableFromSnapshot<Diary> {
        override fun createFrom(snapshot: DocumentSnapshot): Diary {
            val createdOn = snapshot.get("createdOn", Timestamp::class.java)
            val name = snapshot.getString("name")

            if (createdOn == null || name == null) {
                throw Error("Incomplete DB entity: ${snapshot.reference.path}")
            }

            return Diary(snapshot.id, createdOn, name)
        }
    }
}
