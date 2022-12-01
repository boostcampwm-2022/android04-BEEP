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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

@SuppressLint("MissingPermission")
class SharedLocationManager constructor(
    private val context: Context
) {
    private val _receivingLocationUpdates = MutableStateFlow(false)
    val receivingLocationUpdates = _receivingLocationUpdates.asStateFlow()

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationUpdates = callbackFlow {
        val locationRequest = LocationRequest.create().apply {
            interval = LOCATION_INTERVAL
            fastestInterval = LOCATION_INTERVAL / 2
            priority = Priority.PRIORITY_HIGH_ACCURACY
            maxWaitTime = LOCATION_INTERVAL
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val lastLocationResult = result.lastLocation ?: return
                Timber.tag("TAG").d("${javaClass.simpleName} lastLocationResult -> $lastLocationResult")
                trySend(VertexLocation(lastLocationResult.longitude, lastLocationResult.latitude))
            }
        }

        if (context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION).not() ||
            context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION).not()
        ) close()

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        ).addOnFailureListener { e ->
            Timber.tag("TAG").d("${javaClass.simpleName} onFailure Location $e")
            close(e)
        }.addOnSuccessListener {
            Timber.tag("TAG").d("${javaClass.simpleName} Start Location Updates")
            _receivingLocationUpdates.value = true
        }

        awaitClose {
            Timber.tag("TAG").d("${javaClass.simpleName} Stopping Location Updates")
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    fun locationFlow() = locationUpdates

    companion object {
        private const val LOCATION_INTERVAL = 1000L
    }
}

fun Context.hasPermission(permission: String): Boolean {
    if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION &&
        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
    ) {
        return true
    }

    return ActivityCompat.checkSelfPermission(this, permission) ==
        PackageManager.PERMISSION_GRANTED
}
