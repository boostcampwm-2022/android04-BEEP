package com.lighthouse.domain.usecase

import com.google.common.truth.Truth
import com.lighthouse.domain.DmsLocation
import com.lighthouse.domain.LocationConverter
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.domain.repository.BrandRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.DisplayName

@ExperimentalCoroutinesApi
class GetBrandPlaceInfosUseCaseTest {

    private val brandRepository: BrandRepository = mockk()
    private lateinit var cardinalLocations: List<DmsLocation>

    @Before
    fun setUp() {
        cardinalLocations = LocationConverter.getCardinalDirections(x, y)
    }

    @Test
    @DisplayName("[성공] 검색 결과를 갖고온다")
    fun getBrandPlaceInfoSuccess() = runTest {
        // given
        val useCase = GetBrandPlaceInfosUseCase(brandRepository)
        cardinalLocations.forEach { location ->
            brandKeyword.forEach { brandName ->
                coEvery {
                    brandRepository.getBrandPlaceInfo(brandName, location.x, location.y, 5)
                } returns Result.success(brandResult)
            }
        }

        // when
        val action = useCase(brandKeyword, x, y, 5).getOrThrow()

        // then
        for (brandPlaceInfo in action) {
            Truth.assertThat(brandPlaceInfo).isEqualTo(brandPlaceInfoResults)
        }
    }

    companion object {
        private const val x = 37.284
        private const val y = 127.1071
        private val brandKeyword = listOf("스타벅스", "베스킨라빈스", "BHC", "BBQ", "GS25", "CU", "아파트", "어린이집", "파파존스")
        private val brandResult = brandKeyword.map {
            BrandPlaceInfo("서울 중구", "스타벅스", "", "", "", "", "")
        }
        private val brandPlaceInfoResults = BrandPlaceInfo("서울 중구", "스타벅스", "", "", "", "", "")
    }
}
