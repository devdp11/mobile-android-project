package com.example.android_studio_project.data.retrofit.services

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.android_studio_project.data.retrofit.core.API
import com.example.android_studio_project.data.retrofit.interfaces.TripInterface
import com.example.android_studio_project.data.retrofit.models.TripModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TripService(private val context: Context) {
    private val tripApi = API.getRetrofitInstance().create(TripInterface::class.java)
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    }

    fun getTripsByUser(onResponse: (List<TripModel>?) -> Unit, onFailure: (Throwable) -> Unit) {
        val userEmail = getUserEmail()
        if (userEmail != null) {
            val call = tripApi.getTripsByUser(userEmail)
            call.enqueue(object : Callback<List<TripModel>> {
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
        } else {
            onFailure(Throwable("User email not found in SharedPreferences"))
        }
    }

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

    private fun getUserEmail(): String? {
        return sharedPreferences.getString("user_email", null)
    }
}
