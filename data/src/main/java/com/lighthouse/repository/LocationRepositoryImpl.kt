package com.lighthouse.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.lighthouse.domain.Dms
import com.lighthouse.domain.DmsLocation
import com.lighthouse.domain.LocationConverter
import com.lighthouse.domain.VertexLocation
import com.lighthouse.domain.repository.LocationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@SuppressLint("MissingPermission")
class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationRepository {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var lastLocation = MutableStateFlow<VertexLocation?>(null)
    private var dmsLocation = DmsLocation(Dms(0, 0, 0), Dms(0, 0, 0))

    init {
        setLocationClient()
        initLastLocation()
    }

    private fun initLastLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            lastLocation.value = VertexLocation(location.longitude, location.latitude)
            val x = LocationConverter.toMinDms(location.longitude)
            val y = LocationConverter.toMinDms(location.latitude)
            val currentLocation = DmsLocation(x, y)
            dmsLocation = currentLocation
        }
    }

    private fun setLocationClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        locationRequest = LocationRequest.create().apply {
            interval = LOCATION_INTERVAL
            fastestInterval = LOCATION_INTERVAL / 2
            priority = Priority.PRIORITY_HIGH_ACCURACY
            maxWaitTime = LOCATION_INTERVAL
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(context)
        client.checkLocationSettings(builder.build())
    }

    override fun getLocationInterval() = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val lastLocationResult = result.lastLocation ?: return
                val x = LocationConverter.toMinDms(lastLocationResult.longitude)
                val y = LocationConverter.toMinDms(lastLocationResult.latitude)
                val currentLocation = DmsLocation(x, y)

                // 이전의 section에서 벗어난 경우에만 값 방출
                if (dmsLocation != currentLocation) {
                    dmsLocation = currentLocation
                    trySend(VertexLocation(lastLocationResult.longitude, lastLocationResult.latitude))
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        ).addOnFailureListener { e ->
            close(e)
        }

        awaitClose { fusedLocationProviderClient.removeLocationUpdates(locationCallback) }
    }

    override fun getLastLocation() = lastLocation

    companion object {
        private const val LOCATION_INTERVAL = 1000L
    }
}
