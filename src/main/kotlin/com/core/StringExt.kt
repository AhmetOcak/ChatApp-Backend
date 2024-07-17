package com.core

import com.model.GroupType
import com.model.MessageType
import java.lang.IllegalArgumentException

fun String.isValidEmail() : Boolean {
    val emailRegex = Regex("""^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""")
    return emailRegex.matches(this)
}

fun String.toMessageType(): MessageType {
    return when (this.uppercase()) {
        MessageType.TEXT.name -> MessageType.TEXT
        MessageType.AUDIO.name -> MessageType.AUDIO
        MessageType.IMAGE.name -> MessageType.IMAGE
        MessageType.DOC.name -> MessageType.DOC
        else -> throw IllegalArgumentException("Wrong message type $this")
    }
}

fun String.toGroupType(): GroupType {
    return when (this) {
        GroupType.CHAT_GROUP.name -> GroupType.CHAT_GROUP
        GroupType.PRIVATE_CHAT_GROUP.name -> GroupType.PRIVATE_CHAT_GROUP
        else -> throw IllegalArgumentException("Wrong message type $this")
    }
}