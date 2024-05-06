package com.example.android_studio_project.retrofit.DataResponse.UserTrip

import com.example.android_studio_project.retrofit.DataResponse.Trip.TripData
import com.example.android_studio_project.retrofit.DataResponse.User.UserData
import com.google.gson.annotations.SerializedName

data class UserTripData(
    @SerializedName("user") val user: UserData,
    @SerializedName("trip") val trip: TripData
)
