package com.equipo2.model;

import com.google.firebase.Timestamp;

data class ChatMessageModel(
    val message : String?,
    val senderId: String?,
    val timestamp: Timestamp?
)
