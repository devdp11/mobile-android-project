package com.example.android_studio_project.data.retrofit.models

import java.util.UUID

data class TripModel(
    val uuid: UUID?,
    val name: String?,
    val description: String?,
    val startDate: String?,
    val endDate: String?,
    val rating: Float?,
)
