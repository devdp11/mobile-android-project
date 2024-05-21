package com.example.android_studio_project.data.retrofit.interfaces

import com.example.android_studio_project.data.retrofit.models.UserModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthInterface {
    @POST("user/create")
    fun createUser(@Body userData: UserModel): Call<UserModel>

    @GET("user/{email}/{password}")
    fun verifyUser(@Path("email") email: String, @Path("password") password: String): Call<ResponseBody>
}
