package com.example.android_studio_project.data.retrofit.models

import java.util.Date
import java.util.UUID

data class TripModelCreate(
    val uuid: UUID,
    val name: String?,
    val description: String?,
    val startDate: String?,
    val endDate: String?,
    val rating: Float?,
)