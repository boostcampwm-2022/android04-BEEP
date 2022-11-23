package com.lighthouse.database.converter

import androidx.room.TypeConverter
import com.lighthouse.domain.Dms
import com.lighthouse.domain.LocationConverter

class DmsConverter {

    @TypeConverter
    fun decimalToDms(value: Double?): Dms? {
        return value?.let { LocationConverter.toMinDms(it) }
    }

    @TypeConverter
    fun dmsToDecimal(dms: Dms?): Double? {
        return dms?.let { LocationConverter.convertToDD(it) }
    }
}
