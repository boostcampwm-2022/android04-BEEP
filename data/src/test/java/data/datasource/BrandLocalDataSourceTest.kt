package data.datasource

// import android.content.Context
// import androidx.room.Room
// import androidx.test.core.app.ApplicationProvider
// import com.google.common.truth.Truth
// import com.lighthouse.database.BeepDatabase
// import com.lighthouse.database.dao.BrandWithSectionDao
// import com.lighthouse.database.entity.BrandLocationEntity
// import com.lighthouse.database.entity.SectionEntity
// import com.lighthouse.core.utils.location.Dms
// import kotlinx.coroutines.ExperimentalCoroutinesApi
// import kotlinx.coroutines.test.runTest
// import org.junit.After
// import org.junit.Before
// import org.junit.Test
// import org.junit.jupiter.api.DisplayName
// import org.junit.runner.RunWith
// import org.robolectric.RobolectricTestRunner
// import java.util.Date
// import java.util.UUID
//
// @RunWith(RobolectricTestRunner::class)
// @ExperimentalCoroutinesApi
class BrandLocalDataSourceTest {

//    private lateinit var dao: BrandWithSectionDao
//    private lateinit var db: BeepDatabase
//
//    @Before
//    fun createDb() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        db = Room.inMemoryDatabaseBuilder(
//            context,
//            BeepDatabase::class.java
//        ).build()
//        dao = db.brandWithSectionDao()
//    }
//
//    @After
//    fun closeDb() {
//        db.close()
//    }
//
//    @Test
//    @DisplayName("[성공] 룸에 Brand, Section 넣기 성공")
//    fun insertSectionWithBrands() = runTest {
//        // given
//        dao.insertSection(sectionEntity)
//        dao.insertBrand(brandPlaceInfos)
//
//        // when
//        val brandWithSections = dao.getBrands("test")
//
//        println(brandWithSections?.sectionEntity)
//        println(sectionEntity)
//
//        // then
//        brandWithSections?.brands?.forEach {
//            Truth.assertThat(it.sectionId).isEqualTo(brandWithSections.sectionEntity.id)
//        }
//    }
//
//    @Test
//    @DisplayName("[성공] 룸에서 Section을 지우면 Brand들도 지워진다")
//    fun deleteSectionWithBrands() = runTest {
//        // given
//        dao.insertSection(sectionEntity)
//        dao.insertBrand(brandPlaceInfos)
//
//        // when
//        dao.deleteSection("test")
//        val brands = dao.getBrands("test")?.brands
//
//        // then
//        Truth.assertThat(brands.isNullOrEmpty()).isTrue()
//    }
//
//    companion object {
//
//        private val sectionEntity = SectionEntity(
//            id = "test",
//            searchDate = Date(),
//            x = Dms(100, 100, 100),
//            y = Dms(100, 100, 100)
//        )
//
//        private val brandPlaceInfos = listOf(
//            BrandLocationEntity(
//                sectionId = "test",
//                addressName = "경기도 용인시 기흥구",
//                placeName = "경기도 용인시 기흥구",
//                placeUrl = UUID.randomUUID().toString(),
//                categoryName = "test",
//                brand = "스타벅스",
//                x = "210",
//                y = "110"
//            ),
//            BrandLocationEntity(
//                sectionId = "test",
//                addressName = "경기도 용인시 기흥구",
//                placeName = "경기도 용인시 기흥구",
//                placeUrl = UUID.randomUUID().toString(),
//                brand = "스타벅스",
//                categoryName = "test",
//                x = "210",
//                y = "110"
//            ),
//            BrandLocationEntity(
//                sectionId = "test",
//                addressName = "경기도 용인시 기흥구",
//                placeName = "경기도 용인시 기흥구",
//                placeUrl = UUID.randomUUID().toString(),
//                brand = "스타벅스",
//                categoryName = "test",
//                x = "210",
//                y = "110"
//            ),
//            BrandLocationEntity(
//                sectionId = "test",
//                addressName = "경기도 용인시 기흥구",
//                placeName = "경기도 용인시 기흥구",
//                placeUrl = UUID.randomUUID().toString(),
//                brand = "스타벅스",
//                categoryName = "test",
//                x = "210",
//                y = "110"
//            ),
//            BrandLocationEntity(
//                sectionId = "test",
//                addressName = "경기도 용인시 기흥구",
//                placeName = "경기도 용인시 기흥구",
//                placeUrl = UUID.randomUUID().toString(),
//                brand = "스타벅스",
//                categoryName = "test",
//                x = "210",
//                y = "110"
//            )
//        )
//    }
}
