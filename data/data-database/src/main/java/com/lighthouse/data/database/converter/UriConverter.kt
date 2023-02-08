package com.lighthouse.data.database.converter

import android.net.Uri
import androidx.room.TypeConverter

class UriConverter {
    @TypeConverter
    fun fromUri(uri: Uri?): String {
        return uri?.toString() ?: ""
    }

    @TypeConverter
    fun stringToUri(string: String?): Uri? {
        string ?: return null
        if (string == "") {
            return null
        }
        return Uri.parse(string)
    }
}
