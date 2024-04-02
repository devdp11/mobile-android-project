package com.example.android_studio_project.data.ent

import java.util.*

data class Trip(
    val uuid: UUID,
    val description: String,
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val rating: Int,
    val locations: MutableList<Location> = mutableListOf()
)
