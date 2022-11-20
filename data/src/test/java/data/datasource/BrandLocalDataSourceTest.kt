package data.datasource

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import com.lighthouse.database.BeepDatabase
import com.lighthouse.database.dao.BrandWithSectionDao
import com.lighthouse.database.entity.SectionEntity
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.mapper.toEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class BrandLocalDataSourceTest {

    private lateinit var dao: BrandWithSectionDao
    private lateinit var db: BeepDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            BeepDatabase::class.java
        ).build()
        dao = db.brandWithSectionDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    @DisplayName("[성공] 룸에 Brand, Section 넣기 성공")
    fun insertSectionWithBrands() = runTest {
        // given
        dao.insertSectionWithBrands(sectionEntity, brandPlaceInfos)

        // when
        val brandWithSections = dao.getBrands(1L)

        // then
        brandWithSections.forEach { brandWithSection ->
            Truth.assertThat(brandWithSection.sectionEntity).isEqualTo(sectionEntity)
            Truth.assertThat(brandWithSection.brands).isEqualTo(brandPlaceInfos.toEntity(1))

            brandWithSection.brands.forEach {
                Truth.assertThat(it.sectionId).isEqualTo(brandWithSection.sectionEntity.id)
            }
        }
    }

    @Test
    @DisplayName("[성공] 룸에서 Section을 지우면 Brand들도 지워진다")
    fun deleteSectionWithBrands() = runTest {
        // given
        dao.insertSectionWithBrands(sectionEntity, brandPlaceInfos)

        // when
        dao.deleteSection(1L)
        val brands = dao.getBrands(1L)

        // then
        Truth.assertThat(brands.isEmpty()).isTrue()
    }

    companion object {
        private val sectionEntity = SectionEntity(
            minX = "100",
            minY = "200"
        )

        private val brandPlaceInfos = listOf(
            BrandPlaceInfo(
                addressName = "경기도 용인시 기흥구",
                placeName = "경기도 용인시 기흥구",
                placeUrl = "https",
                brand = "스타벅스",
                x = "210",
                y = "110"
            )
        )
    }
}
