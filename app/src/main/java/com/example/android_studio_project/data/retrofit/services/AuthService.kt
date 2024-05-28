package com.example.android_studio_project.data.retrofit.services

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.android_studio_project.data.retrofit.core.API
import com.example.android_studio_project.data.retrofit.interfaces.AuthInterface
import com.example.android_studio_project.data.retrofit.models.UserModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService(private val context: Context) {
    private val authApi = API.getRetrofitInstance().create(AuthInterface::class.java)
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    }

    fun createUser(user: UserModel, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = authApi.createUser(user)
        call.enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.isSuccessful) {
                    user.email?.let { saveUserEmail(it) }
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

    fun verifyUser(email: String, password: String, onResponse: (Boolean) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = authApi.verifyUser(email, password)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    saveUserEmail(email)
                    // Log.d("AuthService", "User email saved: $email")
                    onResponse(true)
                } else {
                    onResponse(false)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    private fun saveUserEmail(email: String) {
        sharedPreferences.edit().putString("user_email", email).apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("user_email", null)
    }

    fun clearUserEmail() {
        sharedPreferences.edit().remove("user_email").apply()
    }
}
