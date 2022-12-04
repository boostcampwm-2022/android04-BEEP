package com.lighthouse.datasource.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.lighthouse.domain.Dms
import com.lighthouse.domain.DmsLocation
import com.lighthouse.domain.LocationConverter
import com.lighthouse.domain.VertexLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import timber.log.Timber

@SuppressLint("MissingPermission")
class SharedLocationManager constructor(
    private val context: Context
) {
    var receivingLocationUpdates = MutableStateFlow(hasLocationPermission())
        private set

    fun locationFlow() = locationUpdates

    fun changePermission(hasPermission: Boolean) {
        receivingLocationUpdates.value = hasPermission
        if (hasPermission) setProviderRequest()
    }

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest = LocationRequest.create().apply {
        interval = LOCATION_INTERVAL
        fastestInterval = LOCATION_INTERVAL / 2
        priority = Priority.PRIORITY_HIGH_ACCURACY
        maxWaitTime = LOCATION_INTERVAL
    }
    private var locationCallback: LocationCallback? = null
    private var prevLocation = DmsLocation(Dms(0, 0, 0), Dms(0, 0, 0))

    private val locationUpdates = callbackFlow {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val locationResult = result.lastLocation ?: return
                val currentLocation = setDmsLocation(locationResult)
                Timber.tag("TAG").d("${javaClass.simpleName} lastLocationResult -> $locationResult")
                if (prevLocation != currentLocation) {
                    prevLocation = currentLocation
                    Timber.tag("TAG").d("${javaClass.simpleName} 로케이션 갱신 $prevLocation")
                    trySend(VertexLocation(locationResult.longitude, locationResult.latitude))
                }
            }
        }

        if (hasLocationPermission().not()) close()

        setProviderRequest()

        awaitClose {
            Timber.tag("TAG").d("${javaClass.simpleName} Stopping Location Updates")
            locationCallback?.let { fusedLocationProviderClient.removeLocationUpdates(it) }
        }
    }.shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.WhileSubscribed())

    private fun hasLocationPermission() =
        context.hasLocationPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
            context.hasLocationPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

    private fun Context.hasLocationPermission(permission: String): Boolean {
        if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION &&
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
        ) {
            return true
        }

        return ActivityCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun setProviderRequest() {
        locationCallback?.let {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            ).addOnFailureListener { e ->
                receivingLocationUpdates.value = false
                Timber.tag("TAG").d("${javaClass.simpleName} onFailure Location $e")
            }.addOnSuccessListener {
                receivingLocationUpdates.value = true
                Timber.tag("TAG").d("${javaClass.simpleName} Start Location Updates")
            }
        }
    }

    private fun setDmsLocation(lastLocationResult: Location): DmsLocation {
        val x = LocationConverter.toMinDms(lastLocationResult.longitude)
        val y = LocationConverter.toMinDms(lastLocationResult.latitude)
        return DmsLocation(x, y)
    }

    companion object {
        private const val LOCATION_INTERVAL = 30000L
    }
}
