package com.example.android_studio_project.data.retrofit.services

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.android_studio_project.data.retrofit.core.API
import com.example.android_studio_project.data.retrofit.interfaces.UserInterface
import com.example.android_studio_project.data.retrofit.models.UpdateUserModel
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

    fun updateUser(user: UserModel, onResponse: (UserModel?) -> Unit, onFailure: (Throwable) -> Unit) {
        val userEmail = user.email
        if (userEmail != null) {
            val updateUserModel = UpdateUserModel(email = userEmail, userData = user)
            val call = userApi.updateUser(updateUserModel)
            call.enqueue(object : Callback<UserModel> {
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                    if (response.isSuccessful) {
                        val updatedUser = response.body()
                        onResponse(updatedUser)
                        //Log.d("UserService", "User updated successfully")
                    } else {
                        onFailure(Throwable("Failed to update user: ${response.code()}"))
                        //Log.e("UserService", "Failed to update user: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    onFailure(t)
                    //Log.e("UserService", "Failed to update user", t)
                }
            })
        } else {
            onFailure(Throwable("User email not provided"))
            //Log.e("UserService", "User email not provided")
        }
    }

    fun updateUserPassword(newPassword: String, onResponse: () -> Unit, onFailure: (Throwable) -> Unit) {
        val userEmail = getUserEmail()
        if (userEmail != null) {
            val call = userApi.updateUserPassword(userEmail, newPassword)
            call.enqueue(object : Callback<UserModel> {
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                    if (response.isSuccessful) {
                        onResponse()
                        //Log.d("UserService", "User password updated successfully")
                    } else {
                        onFailure(Throwable("Failed to update user password: ${response.code()}"))
                        //Log.e("UserService", "Failed to update user password: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    onFailure(t)
                    //Log.e("UserService", "Failed to update user password", t)
                }
            })
        } else {
            onFailure(Throwable("User email not found in SharedPreferences"))
            //Log.e("UserService", "User email not found in SharedPreferences")
        }
    }


    private fun getUserEmail(): String? {
        return sharedPreferences.getString("user_email", null)
    }
}