package com.vidyarthibus.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class LocationHelper(context: Context) {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? = try {
        val cts = CancellationTokenSource()
        client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token).await()
    } catch (e: Exception) {
        null
    }

    /**
     * Returns true if [location] is within [radiusMeters] of any point in [routePoints].
     * In production, routePoints come from Firebase. Here we use generous defaults so
     * the emulator (which reports lat=0,lon=0) is not blocked.
     */
    fun isNearRoute(location: Location, routeId: String, radiusMeters: Float = 2000f): Boolean {
        val centres = routeCentres[routeId] ?: return true   // unknown route → allow
        return centres.any { (lat, lon) ->
            val result = FloatArray(1)
            Location.distanceBetween(location.latitude, location.longitude, lat, lon, result)
            result[0] <= radiusMeters
        }
    }

    companion object {
        // Approximate centre points for each route (lat, lon)
        private val routeCentres = mapOf(
            "route_01" to listOf(Pair(13.00, 77.60)),
            "route_02" to listOf(Pair(12.95, 77.55)),
            "route_03" to listOf(Pair(13.05, 77.65))
        )
    }
}
