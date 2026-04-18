package com.univ.routeplanner.data.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OrsApiService {

    @GET("v2/directions/driving-car")
    suspend fun getDrivingRoute(
        @Header("Authorization") apiKey: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): RouteResponse
}