package com.lighthouse

import android.app.Application
import androidx.room.Room
import com.lighthouse.beep.BuildConfig
import com.lighthouse.database.BeepDatabase
import com.lighthouse.database.BeepDatabase.Companion.DATABASE_NAME
import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.presentation.util.UUID
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date

@HiltAndroidApp
class BeepApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(CustomTimberTree())
        }

        val room = Room.databaseBuilder(applicationContext, BeepDatabase::class.java, DATABASE_NAME).build()
        val dao = room.gifticonDao()

        val gifticonTestData = listOf(
            GifticonEntity(UUID.generate(), "Guest", false, "Guest", "스타벅스", Date(120, 20, 20), "bar", true, 1, "memo", true),
            GifticonEntity(UUID.generate(), "Guest", false, "Guest", "베스킨라빈스", Date(122, 11, 15), "bar", true, 1, "memo", true),
            GifticonEntity(UUID.generate(), "Guest", false, "Guest", "BHC", Date(122, 5, 10), "bar", true, 1, "memo", false),
            GifticonEntity(UUID.generate(), "Guest", false, "Guest", "GS25", Date(160, 10, 20), "bar", true, 1, "memo", false),
            GifticonEntity(UUID.generate(), "Guest", false, "Guest", "CU", Date(160, 10, 20), "bar", true, 1, "memo", true),
            GifticonEntity(UUID.generate(), "Guest", false, "Guest", "서브웨이", Date(160, 10, 20), "bar", true, 1, "memo", true),
            GifticonEntity(UUID.generate(), "Guest", false, "Guest", "서브웨이", Date(160, 10, 20), "bar", true, 1, "memo", false),
            GifticonEntity(UUID.generate(), "Guest", false, "Guest", "서브웨이", Date(160, 10, 20), "bar", true, 1, "memo", false),
            GifticonEntity(UUID.generate(), "Guest", false, "Guest", "서브웨이", Date(160, 10, 20), "bar", true, 1, "memo", false),
            GifticonEntity(UUID.generate(), "Guest", false, "Guest", "서브웨이", Date(160, 10, 20), "bar", true, 1, "memo", true),
            GifticonEntity(UUID.generate(), "Guest", false, "Guest", "세븐일레븐", Date(160, 10, 20), "bar", true, 1, "memo", true),
            GifticonEntity(UUID.generate(), "Gㅎuest", false, "Guest", "파파존스", Date(160, 10, 20), "bar", true, 1, "memo", true)
        )

        CoroutineScope(Dispatchers.Main).launch {
            dao.insertGifticon(*gifticonTestData.toTypedArray())
        }
    }
}

class CustomTimberTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return "${element.className}:${element.lineNumber}#${element.methodName}"
    }
}
