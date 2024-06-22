package com.starorigins.stockify.widgetapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("login")
    fun loginFun(@Body loginRequest: LoginRequest?): Call<LoginResponse>

    @POST("sign-up")
    fun registerFun(@Body registerRequest: RegisterRequest?): Call<RegisterResponse>


}
