package com.example.android_studio_project.retrofit.DataResponse.Location

import com.google.gson.annotations.SerializedName

data class LocationData(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    // @SerializedName("type") val type: LocationType,
    @SerializedName("rating") val rating: Float,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    // @SerializedName("photos") val photos: List<Photo>?
)