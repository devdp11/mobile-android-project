package com.example.android_studio_project.data.retrofit.services

import android.content.Context
import com.example.android_studio_project.data.retrofit.core.API
import com.example.android_studio_project.data.retrofit.interfaces.TripInterface
import com.example.android_studio_project.data.retrofit.models.TripModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class TripService(private val context: Context) {
    private val tripApi = API.getRetrofitInstance().create(TripInterface::class.java)

    fun getAllTrips(onResponse: (List<TripModel>?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = tripApi.getTrips()
        call.enqueue(object : Callback<List<TripModel>> {
            override fun onResponse(call: Call<List<TripModel>>, response: Response<List<TripModel>>) {
                if (response.isSuccessful) {
                    val trips = response.body()
                    //Log.d("TripService", "Received trips: $trips")
                    onResponse(trips)
                } else {
                    val error = "Failed to get trips: ${response.code()} ${response.message()}"
                    //Log.e("TripService", error)
                    onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<List<TripModel>>, t: Throwable) {
                //Log.e("TripService", "Error: ${t.message}")
                onFailure(t)
            }
        })
    }

    fun getTripById(uuid: UUID, onResponse: (TripModel?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = tripApi.getTripById(uuid)
        call.enqueue(object : Callback<TripModel> {
            override fun onResponse(call: Call<TripModel>, response: Response<TripModel>) {
                if (response.isSuccessful) {
                    val trip = response.body()
                    onResponse(trip)
                } else {
                    onFailure(Throwable("Failed to get trip details: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<TripModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun getUserTrips(userId: String?, onResponse: (List<TripModel>?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = userId?.let { tripApi.getUserTrips(it) }
        call?.enqueue(object : Callback<List<TripModel>> {
            override fun onResponse(call: Call<List<TripModel>>, response: Response<List<TripModel>>) {
                if (response.isSuccessful) {
                    val trips = response.body()
                    onResponse(trips)
                } else {
                    onFailure(Throwable("Failed to get user trips: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<TripModel>>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun deleteTrip(userUuid: String?, tripUuid: UUID, onResponse: () -> Unit, onFailure: (Throwable) -> Unit) {
        val call = tripApi.deleteTrip(userUuid, tripUuid)
        call.enqueue(object : Callback<TripModel> {
            override fun onResponse(call: Call<TripModel>, response: Response<TripModel>) {
                if (response.isSuccessful) {
                    onResponse()
                } else {
                    onFailure(Throwable("Failed to delete trip: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<TripModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }
}
