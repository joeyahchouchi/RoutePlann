package com.univ.routeplanner.data.repository

import com.google.gson.Gson
import com.univ.routeplanner.BuildConfig
import com.univ.routeplanner.data.api.OrsApiService
import com.univ.routeplanner.data.api.RetrofitClient
import com.univ.routeplanner.data.db.RouteDao
import com.univ.routeplanner.data.db.RouteEntity

class RouteRepository(
    private val api: OrsApiService = RetrofitClient.api,
    private val dao: RouteDao
) {

    private val gson = Gson()

    suspend fun getRoute(origin: String, destination: String): RouteResult {
        val response = api.getDrivingRoute(
            apiKey = BuildConfig.ORS_API_KEY,
            start = origin,
            end = destination
        )

        val feature = response.features?.firstOrNull()
        val segment = feature?.properties?.segments?.firstOrNull()
            ?: throw IllegalStateException("No route data returned from API")

        val geometryJson = gson.toJson(feature.geometry?.coordinates ?: emptyList<List<Double>>())

        val entity = RouteEntity(
            origin = origin,
            destination = destination,
            distanceMeters = segment.distance,
            durationSeconds = segment.duration,
            geometryJson = geometryJson,
            fetchedAt = System.currentTimeMillis()
        )
        dao.insert(entity)

        return RouteResult(
            distanceMeters = segment.distance,
            durationSeconds = segment.duration,
            origin = origin,
            destination = destination,
            source = "live API"
        )
    }

    suspend fun getLatestCachedRoute(): RouteResult? {
        val entity = dao.getLatestRoute() ?: return null
        return RouteResult(
            distanceMeters = entity.distanceMeters,
            durationSeconds = entity.durationSeconds,
            origin = entity.origin,
            destination = entity.destination,
            source = "offline cache"
        )
    }
}

data class RouteResult(
    val distanceMeters: Double,
    val durationSeconds: Double,
    val origin: String,
    val destination: String,
    val source: String
)