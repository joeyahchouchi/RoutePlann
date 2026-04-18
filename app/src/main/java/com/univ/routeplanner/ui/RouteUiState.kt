package com.univ.routeplanner.ui

sealed class RouteUiState {
    data object Idle : RouteUiState()
    data object Loading : RouteUiState()
    data class Success(
        val distanceKm: Double,
        val durationMin: Double,
        val source: String
    ) : RouteUiState()
    data class OfflineFallback(
        val distanceKm: Double,
        val durationMin: Double,
        val message: String   // e.g. "No internet — showing last saved route"
    ) : RouteUiState()
    data class Error(val message: String) : RouteUiState()
}