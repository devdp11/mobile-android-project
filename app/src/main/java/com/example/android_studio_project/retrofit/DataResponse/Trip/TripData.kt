package com.example.android_studio_project.retrofit.DataResponse.Trip

import com.example.android_studio_project.retrofit.DataResponse.TripLocation.TripLocationData
import com.example.android_studio_project.retrofit.DataResponse.UserTrip.UserTripData
import com.google.gson.annotations.SerializedName

data class TripData(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("description") val description: String,
    @SerializedName("name") val name: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("users") val users: List<UserTripData>,
    @SerializedName("tripLocations") val tripLocations: List<TripLocationData>
)