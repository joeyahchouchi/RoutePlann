package com.univ.routeplanner.data.api

import com.google.gson.annotations.SerializedName

data class RouteResponse(
    @SerializedName("type") val type: String?,
    @SerializedName("features") val features: List<Feature>?
)

data class Feature(
    @SerializedName("properties") val properties: Properties?,
    @SerializedName("geometry") val geometry: Geometry?
)

data class Properties(
    @SerializedName("segments") val segments: List<Segment>?,
    @SerializedName("summary") val summary: Summary?
)

data class Segment(
    @SerializedName("distance") val distance: Double,
    @SerializedName("duration") val duration: Double
)

data class Summary(
    @SerializedName("distance") val distance: Double,
    @SerializedName("duration") val duration: Double
)

data class Geometry(
    @SerializedName("type") val type: String?,
    @SerializedName("coordinates") val coordinates: List<List<Double>>?
)