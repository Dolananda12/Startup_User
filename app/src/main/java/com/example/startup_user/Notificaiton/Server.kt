package com.example.todoapp.Notificaiton

import com.example.startup_user.NotificationDetail
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Server {
    @Headers("Content-Type: application/json")
    @POST("/send")
    suspend fun sendTasks(@Body tasks: NotificationDetail): Response<Unit>
}