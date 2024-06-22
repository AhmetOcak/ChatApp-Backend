package com.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.model.NotificationContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun sendFcmMessage(notificationContent: NotificationContent, token: String) {
    return withContext(Dispatchers.IO) {
        try {
            val message = Message.builder()
                .setNotification(
                    Notification.builder()
                        .setTitle(notificationContent.title)
                        .setBody(notificationContent.message)
                        .build()
                )
                .setToken(token)
                .build()

            FirebaseMessaging.getInstance().send(message)
        } catch (e: Exception) {
            println(e.stackTraceToString())
        }
    }
}