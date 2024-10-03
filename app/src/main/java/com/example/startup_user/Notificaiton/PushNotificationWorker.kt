package com.example.todoapp.Notificaiton

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.startup_user.Notificaiton.sendNotification1

class PushNotificationWorker (context: Context,parameters: WorkerParameters) : CoroutineWorker(context,parameters){
    override suspend fun doWork(): Result {
        println("recieved packet:"+inputData.keyValueMap)
       val title = inputData.getString("title")
       val body = inputData.getString("body")
       val deepLink = inputData.getString("deeplink")
      println(body)
      sendNotification1(title!!,body!!,deepLink!!, context = applicationContext )
    return Result.success()
    }
}