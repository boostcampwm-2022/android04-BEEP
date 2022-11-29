package com.lighthouse.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.app.ActivityCompat
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
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@SuppressLint("MissingPermission")
class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationRepository {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var gpsListener: LocationListener? = null
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var lastLocation = MutableStateFlow<VertexLocation?>(null)
    private var dmsLocation = DmsLocation(Dms(0, 0, 0), Dms(0, 0, 0))

    init {
        setLocationClient()
        initLastLocation()
    }

    private fun setLocationClient() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
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
    }

    private fun initLastLocation() {
        if (checkPermission().not()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            fusedLocationProviderClient?.lastLocation?.addOnSuccessListener { location ->
                setLastLocation(location)
            }
        } else {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: return
            setLastLocation(location)
        }
    }

    private fun setLastLocation(location: Location) {
        lastLocation.value = VertexLocation(location.longitude, location.latitude)
        val currentLocation = setDmsLocation(location)
        dmsLocation = currentLocation
    }

    override fun getLocationInterval() = callbackFlow {
        if (checkPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val lastLocationResult = result.lastLocation ?: return
                        sendLocation(lastLocationResult)
                    }
                }

                fusedLocationProviderClient?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )?.addOnFailureListener { e ->
                    close(e)
                }
            } else {
                gpsListener = LocationListener() { location ->
                    sendLocation(location)
                }
                gpsListener?.let {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        LOCATION_INTERVAL,
                        LOCATION_DISTANCE,
                        it
                    )
                }
            }
        }
        awaitClose {
            gpsListener?.let { locationManager.removeUpdates(it) }
            fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
        }
    }

    private fun ProducerScope<VertexLocation>.sendLocation(location: Location) {
        val currentLocation = setDmsLocation(location)
        if (dmsLocation != currentLocation) {
            dmsLocation = currentLocation
            trySend(VertexLocation(location.longitude, location.latitude))
        }
    }

    private fun setDmsLocation(lastLocationResult: Location): DmsLocation {
        val x = LocationConverter.toMinDms(lastLocationResult.longitude)
        val y = LocationConverter.toMinDms(lastLocationResult.latitude)
        return DmsLocation(x, y)
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    override fun getLastLocation() = lastLocation

    companion object {
        private const val LOCATION_INTERVAL = 10000L
        private const val LOCATION_DISTANCE = 200.0f
    }
}
