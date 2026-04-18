package com.univ.routeplanner.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val origin: String,           // "lng,lat"
    val destination: String,      // "lng,lat"
    val distanceMeters: Double,
    val durationSeconds: Double,
    val geometryJson: String,     // the full coordinates array, stored as JSON text
    val fetchedAt: Long           // System.currentTimeMillis() when saved
)