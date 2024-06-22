package com.example.android_studio_project.data.retrofit.services

import android.content.Context
import android.util.Log
import com.example.android_studio_project.data.retrofit.core.API
import com.example.android_studio_project.data.retrofit.interfaces.LocationInterface
import com.example.android_studio_project.data.retrofit.models.LocationModelCreate
import com.example.android_studio_project.data.retrofit.models.LocationTypeModel
import com.example.android_studio_project.data.retrofit.models.PhotoModel
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

    fun updateLocation(locationUuid: UUID, location: LocationModelCreate, onResponse: (String, UUID?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = locationApi.updateLocation(locationUuid, location)
        call.enqueue(object : Callback<LocationModelCreate> {
            override fun onResponse(call: Call<LocationModelCreate>, response: Response<LocationModelCreate>) {
                if (response.isSuccessful) {
                    val locationResponse = response.body()
                    val locationUUID = locationResponse?.uuid
                    onResponse("success", locationUUID)
                } else {
                    onFailure(Throwable("Error updating location: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<LocationModelCreate>, t: Throwable) {
                onFailure(t)
            }
        })
    }


    fun createLocation(location: LocationModelCreate, onResponse: (String, UUID?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = locationApi.createLocation(location)
        call.enqueue(object : Callback<LocationModelCreate> {
            override fun onResponse(call: Call<LocationModelCreate>, response: Response<LocationModelCreate>) {
                if (response.isSuccessful) {
                    val locationResponse = response.body()
                    val locationUUID = locationResponse?.uuid
                    onResponse("success", locationUUID)
                } else {
                    onFailure(Throwable("Error creating location: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<LocationModelCreate>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun getLocationById(locationUuid: UUID, onResponse: (LocationModelCreate?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = locationApi.getLocationById(locationUuid)
        call.enqueue(object : Callback<LocationModelCreate> {
            override fun onResponse(call: Call<LocationModelCreate>, response: Response<LocationModelCreate>) {
                if (response.isSuccessful) {
                    val locationDetails = response.body()
                    onResponse(locationDetails)
                } else {
                    val error = "Failed to get location details: ${response.code()} ${response.message()}"
                    onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<LocationModelCreate>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun getPhotoByLocationId(locationUuid: UUID, onResponse: (List<PhotoModel>?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = locationApi.getPhotoByLocationId(locationUuid)
        call.enqueue(object : Callback<List<PhotoModel>> {
            override fun onResponse(call: Call<List<PhotoModel>>, response: Response<List<PhotoModel>>) {
                if (response.isSuccessful) {
                    val photoList = response.body()
                    onResponse(photoList)
                } else {
                    val error = "Failed to get photos: ${response.code()} ${response.message()}"
                    onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<List<PhotoModel>>, t: Throwable) {
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

    fun createPhoto(photo: PhotoModel, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = locationApi.createPhoto(photo)
        call.enqueue(object : Callback<PhotoModel> {
            override fun onResponse(call: Call<PhotoModel>, response: Response<PhotoModel>) {
                if (response.isSuccessful) {
                    onResponse("success")
                } else {
                    onFailure(Throwable("Error creating photo: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<PhotoModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun deleteLocation(tripUuid: UUID, locationUuid: UUID, onResponse: () -> Unit, onFailure: (Throwable) -> Unit) {
        val call = locationApi.deleteLocation(tripUuid, locationUuid)
        call.enqueue(object : Callback<TripLocationModel> {
            override fun onResponse(call: Call<TripLocationModel>, response: Response<TripLocationModel>) {
                if (response.isSuccessful) {
                    onResponse()
                } else {
                    onFailure(Throwable("Failed to delete location: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<TripLocationModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }
}