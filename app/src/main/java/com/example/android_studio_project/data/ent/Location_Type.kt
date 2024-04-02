package com.example.android_studio_project.data.ent

data class LocationType(
    val id: Int,
    val name: String
) {
    companion object {
        val predefinedTypes = listOf(
            LocationType(1, "Restaurant"),
            LocationType(2, "Museum"),
            LocationType(3, "Park"),
            LocationType(4, "Beach"),
            LocationType(5, "Mountain"),
            LocationType(6, "Lake"),
            LocationType(7, "City"),
            LocationType(8, "Landmark"),
            LocationType(9, "Countryside"),
            LocationType(10, "Island"),
            LocationType(11, "Historic Site"),
            LocationType(12, "Forest"),
            LocationType(13, "Desert"),
            LocationType(14, "Village"),
            LocationType(15, "Farm"),
            LocationType(16, "Camping Site"),
            LocationType(17, "Amusement Park"),
            LocationType(18, "Zoo"),
            LocationType(19, "Shopping Mall"),
            LocationType(20, "Market"),
            LocationType(21, "Waterfall"),
            LocationType(22, "Cave"),
            LocationType(23, "Harbor"),
            LocationType(24, "Castle"),
            LocationType(25, "Church"),
            LocationType(26, "Mosque"),
            LocationType(27, "Temple"),
            LocationType(28, "Synagogue"),
            LocationType(29, "Library"),
            LocationType(30, "University"),
            LocationType(31, "Hospital"),
            LocationType(32, "Stadium"),
            LocationType(33, "Theater"),
            LocationType(34, "Concert Hall"),
            LocationType(35, "Cinema"),
            LocationType(36, "Beach Resort"),
            LocationType(37, "Ski Resort"),
            LocationType(38, "Spa"),
            LocationType(39, "Golf Course"),
            LocationType(40, "Race Track"),
            LocationType(41, "Other")
        )
    }
}
