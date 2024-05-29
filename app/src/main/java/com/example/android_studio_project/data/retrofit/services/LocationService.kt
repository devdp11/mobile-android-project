package com.example.android_studio_project.data.retrofit.services

import android.content.Context
import android.util.Log
import com.example.android_studio_project.data.retrofit.core.API
import com.example.android_studio_project.data.retrofit.interfaces.LocationInterface
import com.example.android_studio_project.data.retrofit.models.LocationModel
import com.example.android_studio_project.data.retrofit.models.LocationTypeModel
import com.example.android_studio_project.data.retrofit.models.TripLocationModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class LocationService(private val context: Context) {
    private val locationApi = API.getRetrofitInstance().create(LocationInterface::class.java)

    fun getAllTypes(onResponse: (List<LocationTypeModel>?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = locationApi.getTypes()
        call.enqueue(object : Callback<List<LocationTypeModel>> {
            override fun onResponse(call: Call<List<LocationTypeModel>>, response: Response<List<LocationTypeModel>>) {
                if (response.isSuccessful) {
                    val types = response.body()
                    onResponse(types)
                } else {
                    val error = "Failed to get trips: ${response.code()} ${response.message()}"
                    onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<List<LocationTypeModel>>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun createLocation(location: LocationModel, onResponse: (String, UUID?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = locationApi.createLocation(location)
        call.enqueue(object : Callback<LocationModel> {
            override fun onResponse(call: Call<LocationModel>, response: Response<LocationModel>) {
                if (response.isSuccessful) {
                    val locationResponse = response.body()
                    val locationUUID = locationResponse?.uuid
                    Log.d("createLocation", "Location created successfully. UUID: $locationUUID")
                    onResponse("success", locationUUID)
                } else {
                    onFailure(Throwable("Error creating location: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<LocationModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun createTripLocation(tripLocation: TripLocationModel, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = locationApi.createTripLocation(tripLocation)
        call.enqueue(object : Callback<TripLocationModel> {
            override fun onResponse(call: Call<TripLocationModel>, response: Response<TripLocationModel>) {
                if (response.isSuccessful) {
                    onResponse("success")
                } else {
                    onFailure(Throwable("Error creating trip location: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<TripLocationModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }
}