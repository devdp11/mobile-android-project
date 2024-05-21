package com.example.android_studio_project.data.retrofit.services

import android.content.Context
import android.content.SharedPreferences
import com.example.android_studio_project.data.retrofit.core.API
import com.example.android_studio_project.data.retrofit.interfaces.UserInterface
import com.example.android_studio_project.data.retrofit.models.UserModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserService(private val context: Context) {
    private val userApi = API.getRetrofitInstance().create(UserInterface::class.java)
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    }

    fun getUserDetails(onResponse: (UserModel?) -> Unit, onFailure: (Throwable) -> Unit) {
        val userEmail = getUserEmail()
        if (userEmail != null) {
            val call = userApi.getUser(userEmail)
            call.enqueue(object : Callback<UserModel> {
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        onResponse(user)
                    } else {
                        onFailure(Throwable("Failed to get user details: ${response.code()}"))
                    }
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    onFailure(t)
                }
            })
        } else {
            onFailure(Throwable("User email not found in SharedPreferences"))
        }
    }

    private fun getUserEmail(): String? {
        return sharedPreferences.getString("user_email", null)
    }
}
