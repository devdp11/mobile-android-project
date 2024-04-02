package com.example.android_studio_project.data.ent
import java.util.*

data class Location(
    val uuid: UUID,
    val name: String,
    val description: String,
    val type: Int,
    val rating: Int,
    val latitude: Double,
    val longitude: Double,
    val photos: MutableList<Photo> = mutableListOf()
)
