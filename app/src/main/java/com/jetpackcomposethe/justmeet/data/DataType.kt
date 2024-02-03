package com.jetpackcomposethe.justmeet.data

data class UserData(
    val userId: String? = null,
    val name: String? = null,
    val number: String? = null,
    val email:String?=null,
    val password:String?=null,
    val imageurl: String? = null
) {
    fun toMap(): Map<String, String?> = mapOf(
        "userId" to userId,
        "name" to name,
        "number" to number,
        "email" to email,
        "password" to password,
        "imageurl" to imageurl
    )
}

data class ChatData(
    val chatId:String?="",
    val user1: ChatUser= ChatUser(),
    val user2: ChatUser=ChatUser()
    )

data class ChatUser(
    val userId:String?="",
    val name: String?="",
    val imageUrl: String?="",
    val number: String?="",

    )


data class Message(
    val sendby:String?="",
    val message:String?="",
    val timestamp:String?="",

    )

data class Status(val user:ChatUser=ChatUser(),
    val imageUrl: String?="",
    val timestamp: Long?=null

)