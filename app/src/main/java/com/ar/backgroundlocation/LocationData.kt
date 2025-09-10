package com.ar.backgroundlocation
// LocationData.kt
data class LocationData(
    val driverId: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis() // Opcional, pero útil
    // Puedes añadir más datos si los necesitas, como accuracy, speed, etc.
)
