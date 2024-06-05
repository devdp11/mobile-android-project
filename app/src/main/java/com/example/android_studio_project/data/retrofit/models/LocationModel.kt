package com.example.android_studio_project.data.retrofit.models

import java.util.Date
import java.util.UUID

data class LocationModel(
    val uuid: UUID,
    val name: String?,
    val description: String?,
    val typeId: UUID?,
    val rating: Float?,
    val latitude: Number?,
    val longitude: Number?,
    val date: Date
)
