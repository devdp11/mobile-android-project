package com.example.android_studio_project.data.retrofit.models

import java.util.UUID

data class PhotoModel(
    val uuid: UUID,
    val data: String?,
    val locationId: String?
)
