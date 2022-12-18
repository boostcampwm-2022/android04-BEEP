package com.lighthouse.datasource.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.lighthouse.domain.VertexLocation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@SuppressLint("MissingPermission")
class SharedLocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.create().apply {
        interval = LOCATION_INTERVAL
        fastestInterval = LOCATION_INTERVAL / 2
        priority = Priority.PRIORITY_HIGH_ACCURACY
        maxWaitTime = WAITE_TIME
    }

    private val locationUpdates = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val locationResult = result.lastLocation ?: return
                trySend(VertexLocation(locationResult.longitude, locationResult.latitude))
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        ).addOnFailureListener {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }

        awaitClose {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    fun locationFlow() = locationUpdates

    companion object {
        private const val LOCATION_INTERVAL = 30000L
        private const val WAITE_TIME = 2000L
    }
}
