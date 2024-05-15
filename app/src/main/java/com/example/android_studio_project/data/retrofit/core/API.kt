package com.example.android_studio_project.data.retrofit.core

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class API {
    companion object {
        private val retrofitInstance = Retrofit.Builder()
            .baseUrl("https://api-commov.vercel.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fun getRetrofitInstance(): Retrofit {
            return retrofitInstance
        }
    }
}
