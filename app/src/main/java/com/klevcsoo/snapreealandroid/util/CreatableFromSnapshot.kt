package com.klevcsoo.snapreealandroid.util

import com.google.firebase.firestore.DocumentSnapshot

interface CreatableFromSnapshot<T : Identifiable> {
    fun createFrom(snapshot: DocumentSnapshot): T
}
