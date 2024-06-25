package com.core

import com.model.Message
import com.model.MessageType

fun setContent(message: Message): String {
    return when (message.messageType) {
        MessageType.TEXT -> message.messageContent
        MessageType.IMAGE -> "🖼"
        MessageType.AUDIO-> "🎵"
        MessageType.DOC -> "📕"
    }
}