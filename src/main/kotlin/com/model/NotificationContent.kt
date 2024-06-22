package com.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationContent(
    val title: String,
    val message: String
)
