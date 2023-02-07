package com.lighthouse.common.utils.geography

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.lighthouse.beep.model.location.VertexLocation
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Suppress("DEPRECATION")
class Geography @Inject constructor(@ApplicationContext private val context: Context) {

    // 위도 경도로 주소 구하는 Reverse-GeoCoding
    fun getAddress(position: VertexLocation?): String {
        position ?: return ""

        val geoCoder = Geocoder(context)
        var addr = "-"

        // GRPC 오류 대응
        try {
            var location: Address? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geoCoder.getFromLocation(position.latitude, position.longitude, 1) {
                    location = it.first()
                }
            } else {
                location = geoCoder.getFromLocation(position.latitude, position.longitude, 1)?.first()
            }
            addr = listOfNotNull(
                location?.adminArea,
                location?.subAdminArea,
                location?.locality,
                location?.subLocality,
                location?.thoroughfare
            ).joinToString(" ")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return addr
    }
}
