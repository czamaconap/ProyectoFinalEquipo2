package com.equipo2.model2;

import com.google.firebase.Timestamp;

data class ChatMessageModel(
    val message : String?,
    val senderId: String?,
    val timestamp: Timestamp?
)
