package com.example.android_studio_project.data.retrofit.interfaces

import com.example.android_studio_project.data.retrofit.models.LocationModelCreate
import com.example.android_studio_project.data.retrofit.models.LocationTypeModel
import com.example.android_studio_project.data.retrofit.models.PhotoModel
import com.example.android_studio_project.data.retrofit.models.TripLocationModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface LocationInterface {
    @GET("locationType/")
    fun getTypes(): Call<List<LocationTypeModel>>

    @POST("location/create")
    fun createLocation(@Body locationData: LocationModelCreate): Call<LocationModelCreate>

    @POST("tripLocation/create")
    fun createTripLocation(@Body tripLocationData: TripLocationModel): Call<TripLocationModel>

    @POST("photo/create")
    fun createPhoto(@Body photoData: PhotoModel): Call<PhotoModel>

    @DELETE("tripLocation/{tripUuid}/{locationUuid}")
    fun deleteLocation(@Path("tripUuid") tripUuid: UUID, @Path("locationUuid") locationUuid: UUID): Call<TripLocationModel>
}