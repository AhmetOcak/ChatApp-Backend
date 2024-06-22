package com.model

import kotlinx.serialization.Serializable

@Serializable
data class FcmToken(
    val id: Int = 0,
    val email: String,
    val token: String
)
