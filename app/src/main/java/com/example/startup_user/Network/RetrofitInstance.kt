package com.example.startup_user.Network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONStringer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.StringReader
import java.util.concurrent.TimeUnit

class RetrofitInstance(){
    companion object{
        val BASE_URL_1 = "https://mac-narasimha.p.tnnl.in"
        val interceptor = HttpLoggingInterceptor().apply {
            this.level= HttpLoggingInterceptor.Level.BODY
        }
        val gson = GsonBuilder().setLenient().create()
        fun create() : Retrofit {
            val headerInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .build()
                chain.proceed(newRequest)
            }
            val client = OkHttpClient.Builder()
                .hostnameVerifier { _, _ -> true }
                .apply {
                this.addInterceptor(interceptor)
                    .addInterceptor(headerInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(25, TimeUnit.SECONDS)
            }.build()

            return Retrofit.Builder().baseUrl(BASE_URL_1)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
    }
}