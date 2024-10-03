package com.example.startup_user

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDetail(
    val payload : String,
    val token : String,
    val deeplink : String,
    val topic : String
)
