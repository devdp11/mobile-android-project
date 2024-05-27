package com.example.android_studio_project.data.retrofit.interfaces

import com.example.android_studio_project.data.retrofit.models.UpdateUserModel
import com.example.android_studio_project.data.retrofit.models.UserModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserInterface {
    @GET("user/{email}")
    fun getUser(@Path("email") email: String): Call<UserModel>

    @PUT("user/update")
    fun updateUser(@Body updateUserModel: UpdateUserModel): Call<UserModel>

    @PUT("user/{email}/{password}")
    fun updateUserPassword(@Path("email") email: String, @Path("password") password: String): Call<UserModel>
}