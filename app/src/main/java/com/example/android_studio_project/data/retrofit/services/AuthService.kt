package com.example.android_studio_project.data.retrofit.services

import com.example.android_studio_project.data.retrofit.models.UserModel
import com.example.android_studio_project.data.retrofit.interfaces.AuthInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api-commov.vercel.app/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authApi = retrofit.create(AuthInterface::class.java)

    fun createUser(user: UserModel, onResponse: (UserModel?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = authApi.createUser(user)
        call.enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.isSuccessful) {
                    val createdUser = response.body()
                    onResponse(createdUser)
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }
}
