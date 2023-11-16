package com.klevcsoo.snapreealandroid.service

import com.klevcsoo.snapreealandroid.service.impl.FirebaseServiceInstanceImpl

class FirebaseService {
    companion object {
        val instance = FirebaseServiceInstanceImpl()
    }
}
