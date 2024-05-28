package com.example.android_studio_project.data.retrofit.interfaces

import com.example.android_studio_project.data.retrofit.models.LocationTypeModel
import retrofit2.Call
import retrofit2.http.GET

interface LocationInterface {
    @GET("locationType/")
    fun getTypes(): Call<List<LocationTypeModel>>
}