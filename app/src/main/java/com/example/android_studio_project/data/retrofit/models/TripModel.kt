package com.example.android_studio_project.data.retrofit.models

import java.util.Date
import java.util.UUID

data class TripModel(
    val uuid: UUID,
    val name: String?,
    val description: String?,
    val startDate: Date?,
    val endDate: Date?,
    val rating: Float?,
)
