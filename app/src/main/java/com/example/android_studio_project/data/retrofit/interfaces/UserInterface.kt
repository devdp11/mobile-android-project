package com.example.android_studio_project.data.retrofit.interfaces

import com.example.android_studio_project.data.retrofit.models.UserModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserInterface {
    @GET("user/{email}")
    fun getUser(@Path("email") email: String): Call<UserModel>

    @PUT("user/update/{email}")
    fun updateUser(@Path("email") email: String, @Body userData: UserModel): Call<UserModel>
}