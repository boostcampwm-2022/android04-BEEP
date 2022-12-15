package com.lighthouse.datasource.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.lighthouse.domain.VertexLocation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@SuppressLint("MissingPermission")
class SharedLocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    var receivingLocationUpdates = MutableStateFlow(checkPermission())
        private set

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.create().apply {
        interval = LOCATION_INTERVAL
        fastestInterval = LOCATION_INTERVAL / 2
        priority = Priority.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = SMALLEST_DISPLACEMENT_DIFF
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
        ).addOnFailureListener { e ->
            receivingLocationUpdates.value = false
        }.addOnSuccessListener {
            receivingLocationUpdates.value = true
        }

        awaitClose {
            locationCallback.let { fusedLocationProviderClient.removeLocationUpdates(it) }
        }
    }

    private fun checkPermission() =
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context) ||
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, context)

    private fun checkPermission(permission: String, context: Context): Boolean {
        if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION &&
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
        ) {
            return true
        }

        return ActivityCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
    }

    fun locationFlow() = locationUpdates

    fun changePermission(hasPermission: Boolean) {
        receivingLocationUpdates.value = hasPermission
    }

    companion object {
        private const val LOCATION_INTERVAL = 30000L
        private const val WAITE_TIME = 2000L
        private const val SMALLEST_DISPLACEMENT_DIFF = 250F
    }
}
