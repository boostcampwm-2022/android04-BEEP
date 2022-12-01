package com.lighthouse

import android.app.Application
import androidx.room.Room
import com.lighthouse.beep.BuildConfig
import com.lighthouse.database.BeepDatabase
import com.lighthouse.database.BeepDatabase.Companion.DATABASE_NAME
import com.lighthouse.database.entity.GifticonEntity
import com.lighthouse.presentation.util.UUID
import dagger.hilt.android.HiltAndroidApp
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
            GifticonEntity(UUID.generate(), "이름", false, "이름", "스타벅스", Date(120, 20, 20), "bar", true, 1, "memo", true),
            GifticonEntity(UUID.generate(), "이름", false, "이름", "베스킨라빈스", Date(122, 11, 15), "bar", true, 1, "memo", true),
            GifticonEntity(UUID.generate(), "이름", false, "이름", "BHC", Date(122, 5, 10), "bar", true, 1, "memo", true),
            GifticonEntity(UUID.generate(), "이름", false, "이름", "GS25", Date(160, 10, 20), "bar", true, 1, "memo", true),
            GifticonEntity(UUID.generate(), "이름", false, "이름", "CU", Date(160, 10, 20), "bar", true, 1, "memo", true),
            GifticonEntity(UUID.generate(), "이름", false, "이름", "서브웨이", Date(160, 10, 20), "bar", true, 1, "memo", true),
            GifticonEntity(UUID.generate(), "이름", false, "이름", "세븐일레븐", Date(160, 10, 20), "bar", true, 1, "memo", true),
            GifticonEntity(UUID.generate(), "이름", false, "이름", "파파존스", Date(160, 10, 20), "bar", true, 1, "memo", true)
        )

        /*CoroutineScope(Dispatchers.Main).launch {
            dao.insertGifticon(*gifticonTestData.toTypedArray())
        }*/
    }
}

class CustomTimberTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return "${element.className}:${element.lineNumber}#${element.methodName}"
    }
}
