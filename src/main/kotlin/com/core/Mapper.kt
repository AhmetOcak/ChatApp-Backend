package com.core

import com.model.ReceiveMessage
import com.model.SendMessage

fun SendMessage.toReceiveMessage(): ReceiveMessage {
    return ReceiveMessage(
        senderId = senderId,
        senderName = senderName,
        senderImage = senderImage,
        content = content,
        time = time
    )
}