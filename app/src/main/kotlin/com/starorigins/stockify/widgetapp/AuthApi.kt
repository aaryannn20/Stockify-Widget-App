package com.starorigins.stockify.widgetapp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthApi {
    fun getretrofit(): Retrofit {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient: OkHttpClient = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://localhost:5000/user/")
            .client(okHttpClient)
            .build()
    }

    fun userService(): UserService {
        return getretrofit().create(UserService::class.java)
    }
}
