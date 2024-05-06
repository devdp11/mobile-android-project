package com.example.android_studio_project.retrofit.DataResponse.TripLocation

import com.example.android_studio_project.retrofit.DataResponse.Location.LocationData
import com.example.android_studio_project.retrofit.DataResponse.Trip.TripData
import com.google.gson.annotations.SerializedName

data class TripLocationData(
    @SerializedName("trip") val trip: TripData,
    @SerializedName("location") val location: LocationData
)
