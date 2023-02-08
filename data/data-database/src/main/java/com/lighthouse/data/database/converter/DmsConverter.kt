package com.lighthouse.data.database.converter

import androidx.room.TypeConverter
import com.lighthouse.beep.model.location.Dms
import com.lighthouse.common.utils.geography.LocationConverter

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
