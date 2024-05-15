package com.example.android_studio_project.data.retrofit.services

import com.example.android_studio_project.data.retrofit.core.API
import com.example.android_studio_project.data.retrofit.models.UserModel
import com.example.android_studio_project.data.retrofit.interfaces.AuthInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {
    private val authApi = API.getRetrofitInstance().create(AuthInterface::class.java)
    fun createUser(user: UserModel, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = authApi.createUser(user)
        call.enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.isSuccessful) {
                    onResponse("success")
                } else {
                    onFailure(Throwable("Error creating account: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun verifyUser(email: String, password: String, onResponse: (UserModel?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = authApi.verifyUser(email, password)
        call.enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    onResponse(user)
                } else {
                    onFailure(Throwable("Failed to login: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }
}
