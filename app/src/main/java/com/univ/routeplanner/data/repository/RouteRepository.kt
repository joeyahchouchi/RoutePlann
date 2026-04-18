package com.univ.routeplanner.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

        val coordinates: List<List<Double>> = feature.geometry?.coordinates ?: emptyList()
        val geometryJson = gson.toJson(coordinates)
        val geometryPairs = coordinates.mapNotNull { pt ->
            if (pt.size >= 2) Pair(pt[0], pt[1]) else null
        }

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
            source = "live API",
            geometry = geometryPairs
        )
    }

    suspend fun getLatestCachedRoute(): RouteResult? {
        val entity = dao.getLatestRoute() ?: return null

        val listType = object : TypeToken<List<List<Double>>>() {}.type
        val coords: List<List<Double>> = try {
            gson.fromJson(entity.geometryJson, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        val geometryPairs = coords.mapNotNull { pt ->
            if (pt.size >= 2) Pair(pt[0], pt[1]) else null
        }

        return RouteResult(
            distanceMeters = entity.distanceMeters,
            durationSeconds = entity.durationSeconds,
            origin = entity.origin,
            destination = entity.destination,
            source = "offline cache",
            geometry = geometryPairs
        )
    }

    /**
     * Returns every saved route, newest first.
     * Used by the History screen.
     */
    suspend fun getAllHistory(): List<RouteEntity> {
        return dao.getAllRoutes()
    }

    suspend fun clearCache() {
        dao.clearAll()
    }
}

data class RouteResult(
    val distanceMeters: Double,
    val durationSeconds: Double,
    val origin: String,
    val destination: String,
    val source: String,
    val geometry: List<Pair<Double, Double>> = emptyList()
)