package com.equipo2.model;

import com.google.firebase.Timestamp;

data class ChatroomModel(
   var chatroomId : String,
   var userIds: List<String>,
   var lastMessageTimestamp:Timestamp? = Timestamp.now(),
   var lastMessageSenderId:String? = "",
   var lastMessage:String?= ""
) {
   constructor(chatroomId: String, userIds: MutableList<String>) : this(chatroomId, userIds, Timestamp.now(),"", "") {

   }
}
