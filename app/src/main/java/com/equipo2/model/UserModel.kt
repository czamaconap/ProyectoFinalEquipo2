package com.equipo2.model;

import com.google.firebase.Timestamp;

data class UserModel(
    val  phoneNumber: String? =null,
    var  username: String? = null,
    val  createdTimestamp: Timestamp? = Timestamp.now(),
    val  userId: String?= null,
    var  fcmToken: String? = null
)