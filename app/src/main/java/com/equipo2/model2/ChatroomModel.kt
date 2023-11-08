package com.equipo2.model2;

import com.google.firebase.Timestamp;

import java.util.List;

data class ChatroomModel(
   val chatroomId : String?,
   val userIds:List<String>?,
   val lastMessageTimestamp:Timestamp?,
   val lastMessageSenderId:String?,
   val lastMessage:String?
)
