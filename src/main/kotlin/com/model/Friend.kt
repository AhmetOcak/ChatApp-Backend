package com.model

import kotlinx.serialization.Serializable

@Serializable
data class Friend(
    val id: Int,
    val userEmail1: String,
    val userEmail2: String
)