package com.model

import kotlinx.serialization.Serializable

@Serializable
data class Friend(
    val id: Int,
    val userEmail: String?,
    val friendEmail: String?,
    val friendUsername: String?,
    val friendProfilePicUrl: String?
)