package com.example.android_studio_project.data.retrofit.services

import android.content.Context
import android.util.Log
import com.example.android_studio_project.data.retrofit.core.API
import com.example.android_studio_project.data.retrofit.interfaces.LocationInterface
import com.example.android_studio_project.data.retrofit.models.LocationModel
import com.example.android_studio_project.data.retrofit.models.LocationTypeModel
import com.example.android_studio_project.data.retrofit.models.UserModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                    //Log.e("LocationType", error)
                    onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<List<LocationTypeModel>>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun createLocation(location: LocationModel, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = locationApi.createLocation(location)
        call.enqueue(object : Callback<LocationModel> {
            override fun onResponse(call: Call<LocationModel>, response: Response<LocationModel>) {
                if (response.isSuccessful) {
                    onResponse("success")
                    Log.d("add_location", "Response successful: ${response.body()}")
                } else {
                    onFailure(Throwable("Error creating location: ${response.code()}"))
                    Log.e("add_location", "Error creating location. Response code: ${response.code()}")
                }
            }


            override fun onFailure(call: Call<LocationModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }
}