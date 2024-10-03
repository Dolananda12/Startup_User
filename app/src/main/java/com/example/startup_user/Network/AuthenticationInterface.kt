package com.example.startup_user.Network

import android.media.session.MediaSession.Token
import android.provider.ContactsContract.Data
import com.example.startup_user.AuthObject
import com.example.startup_user.FinalKYC
import com.example.startup_user.Month
import com.example.startup_user.Report12
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthenticationInterface {
@Headers("Content-Type: application/json")
@POST("api/user/auth/signup")
suspend fun sendCredentials(@Body data: FinalKYC) : Response<String>
@POST("api/user/auth/signin")
suspend fun verifyCredentials(@Body data:AuthObject) : Response<String>
@POST("api/admin/upload")
suspend fun sendMonthReport(@Body data: Month) :Response<String>
@POST("api/admin/profile")
suspend fun recieveProfile(@Body token: String) : Response<FinalKYC>
@POST("api/admin/report")
suspend fun recieveReports(@Body data: Report12) : Response<List<Month>>
}