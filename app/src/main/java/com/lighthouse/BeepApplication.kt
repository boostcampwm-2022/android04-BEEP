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
            GifticonEntity(
                UUID.generate(),
                0,
                "이름",
                false,
                "아메리카노",
                "스타벅스",
                Date(120, 20, 20),
                "123456781234",
                false,
                0,
                "memo",
                false
            ),
            GifticonEntity(
                UUID.generate(),
                5000,
                "이름",
                false,
                "어머니는 외계인",
                "베스킨라빈스",
                Date(122, 11, 15),
                "1111222233334444",
                false,
                0,
                "memo",
                false
            ),
            GifticonEntity(
                UUID.generate(),
                7000,
                "이름",
                false,
                "핫후라이드",
                "BHC",
                Date(122, 5, 10),
                "13131324242411",
                false,
                1,
                "memo",
                false
            ),
            GifticonEntity(
                UUID.generate(),
                10000,
                "이름",
                false,
                "바나나맛 우유",
                "GS25",
                Date(160, 10, 20),
                "777799997777",
                false,
                1,
                "memo",
                true
            ),
            GifticonEntity(
                UUID.generate(),
                123123123,
                "이름",
                false,
                "30,000원",
                "CU",
                Date(160, 10, 20),
                "123123123123",
                true,
                1,
                "memo",
                false
            ),
            GifticonEntity(
                UUID.generate(),
                1313132,
                "이름",
                false,
                "이름",
                "서브웨이",
                Date(160, 10, 20),
                "224244242211",
                true,
                1,
                "memo",
                true
            ),
            GifticonEntity(
                UUID.generate(),
                44442222,
                "이름",
                false,
                "5,000원",
                "세븐일레븐",
                Date(160, 10, 20),
                "123131231231",
                true,
                5000,
                "memo",
                false
            ),
            GifticonEntity(
                UUID.generate(),
                12312313131,
                "이름",
                false,
                "5만원",
                "파파존스",
                Date(160, 10, 20),
                "1111222233334444",
                true,
                38000,
                "memo",
                false
            )
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
