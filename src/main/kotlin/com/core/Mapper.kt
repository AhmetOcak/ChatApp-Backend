package com.core

import com.model.Message
import com.model.ReceivedMessage

fun ReceivedMessage.toMessage(): Message {
    return Message(
        senderId = senderId,
        senderProfilePicUrl = senderImage,
        roomId = receiverId,
        messageText = content
    )
}
