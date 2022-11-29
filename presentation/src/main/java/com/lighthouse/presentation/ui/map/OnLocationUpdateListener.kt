package com.lighthouse.presentation.ui.map

import android.location.Location

interface OnLocationUpdateListener {
    fun onLocationUpdated(location: Location)
}
