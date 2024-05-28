package com.example.android_studio_project.data.retrofit.services

import android.content.Context
import android.util.Log
import com.example.android_studio_project.data.retrofit.core.API
import com.example.android_studio_project.data.retrofit.interfaces.LocationInterface
import com.example.android_studio_project.data.retrofit.models.LocationTypeModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationTypeService(private val context: Context) {
    private val locationTypeApi = API.getRetrofitInstance().create(LocationInterface::class.java)

    fun getAllTypes(onResponse: (List<LocationTypeModel>?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = locationTypeApi.getTypes()
        call.enqueue(object : Callback<List<LocationTypeModel>> {
            override fun onResponse(call: Call<List<LocationTypeModel>>, response: Response<List<LocationTypeModel>>) {
                if (response.isSuccessful) {
                    val types = response.body()
                    //Log.d("LocationType", "Received types: $types")
                    onResponse(types)
                } else {
                    val error = "Failed to get trips: ${response.code()} ${response.message()}"
                    //Log.e("LocationType", error)
                    onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<List<LocationTypeModel>>, t: Throwable) {
                //Log.e("LocationType", "Error: ${t.message}")
                onFailure(t)
            }
        })
    }
}