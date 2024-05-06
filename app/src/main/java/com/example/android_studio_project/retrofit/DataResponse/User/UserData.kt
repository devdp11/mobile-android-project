package com.example.android_studio_project.retrofit.DataResponse.User

import com.example.android_studio_project.retrofit.DataResponse.UserTrip.UserTripData
import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    // @SerializedName("avatar") val avatar: ByteArray?,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("email") val email: String,
    @SerializedName("type") val type: Boolean,
    @SerializedName("trips") val trips: List<UserTripData>
)
