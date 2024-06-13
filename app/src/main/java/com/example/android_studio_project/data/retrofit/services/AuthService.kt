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
import java.util.UUID

class AuthService(private val context: Context) {
    private val userService = UserService(context)
    private val authApi = API.getRetrofitInstance().create(AuthInterface::class.java)
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    }

    fun createUser(user: UserModel, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = authApi.createUser(user)
        call.enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.isSuccessful) {
                    val userUUID = response.body()?.uuid
                    userUUID?.let { user.email?.let { it1 -> saveUserData(it1, it) } }
                    onResponse("success")
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null && errorBody.contains("exists")) {
                            onResponse("exists")
                        } else {
                            onFailure(Throwable("Error creating account: ${response.code()}"))
                        }
                    } catch (e: Exception) {
                        onFailure(Throwable("Error parsing error response: ${e.message}"))
                    }
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
                    userService.getUserDetails(
                        userEmail = email,
                        onResponse = { userModel ->
                            val userUUID = userModel?.uuid
                            if (userUUID != null) {
                                saveUserData(email, userUUID)
                                onResponse(true)
                            }
                        },
                        onFailure = {
                        }
                    )
                } else {
                    onResponse(false)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    private fun saveUserData(email: String, uuid: UUID) {
        sharedPreferences.edit().apply {
            putString("user_email", email)
            putString("user_uuid", uuid.toString())
            apply()
        }
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("user_email", null)
    }

    fun getUserUUID(): String? {
        return sharedPreferences.getString("user_uuid", null)
    }

    fun clearUserData() {
        sharedPreferences.edit().remove("user_email").remove("user_uuid").apply()
    }
}

