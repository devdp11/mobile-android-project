package com.example.android_studio_project.data.retrofit.interfaces

import com.example.android_studio_project.data.retrofit.models.LocationModel
import com.example.android_studio_project.data.retrofit.models.TripModel
import com.example.android_studio_project.data.retrofit.models.TripModelCreate
import com.example.android_studio_project.data.retrofit.models.UserModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface TripInterface {

    @GET("trip/")
    fun getTrips(): Call<List<TripModel>>

    @GET("trip/{uuid}")
    fun getTripById(@Path("uuid") uuid: UUID): Call<TripModel>

    @DELETE("userTrip/delete/{userUuid}/{tripUuid}")
    fun deleteTrip(@Path("userUuid") userUuid: String?, @Path("tripUuid") tripUuid: UUID): Call<TripModel>

    @GET("userTrip/{userId}/trips")
    fun getUserTrips(@Path("userId") userId: String?): Call<List<TripModel>>

    @POST("userTrip/create")
    fun createUserTrip(@Body tripData: TripModel): Call<TripModel>



    @PUT("userTrip/update/{tripUuid}")
    fun updateTrip(@Path("tripUuid") uuid: UUID): Call<TripModel>

    @GET("tripLocation/{tripId}")
    fun getTripsLocations(@Path("tripId") uuid: UUID): Call<List<LocationModel>>

    @POST("trip/create")
    fun createTrip(@Body tripData: TripModelCreate): Call<TripModelCreate>
}