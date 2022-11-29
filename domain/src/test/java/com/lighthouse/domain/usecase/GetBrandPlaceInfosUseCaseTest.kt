/*
package com.lighthouse.domain.usecase

import com.lighthouse.domain.DmsLocation
import com.lighthouse.domain.LocationConverter
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.domain.model.BeepError
import com.lighthouse.domain.repository.BrandRepository
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before

@ExperimentalCoroutinesApi
class GetBrandPlaceInfosUseCaseTest {

    private val brandRepository: BrandRepository = mockk()
    private lateinit var cardinalLocations: List<DmsLocation>

    @Before
    fun setUp() {
        cardinalLocations = LocationConverter.getCardinalDirections(x, y)
    }

//    @Test
//    @DisplayName("[성공] 검색 결과를 갖고온다")
//    fun getBrandPlaceInfoSuccess() = runTest {
//        // given
//        val useCase = GetBrandPlaceInfosUseCase(brandRepository)
//        cardinalLocations.forEach { location ->
//            coEvery {
//                brandRepository.getBrandPlaceInfo(brandKeyword, location.x, location.y, 5)
//            } returns Result.success(brandPlaceInfoResults)
//        }
//
//        // when
//        val action = useCase(brandKeyword, x, y, 5).getOrThrow()
//
//        // then
//        Truth.assertThat(action).isEqualTo(brandResult)
//    }
//
//    @Test
//    @DisplayName("[실패] 검색 결과가 존재하지 않는다")
//    fun getBrandPlaceInfoNotFound() = runTest {
//        // given
//        val useCase = GetBrandPlaceInfosUseCase(brandRepository)
//        cardinalLocations.forEach { location ->
//            coEvery {
//                brandRepository.getBrandPlaceInfo(brandKeyword, location.x, location.y, 5)
//            } returns Result.success(emptyList())
//        }
//
//        // when
//        val action = useCase(brandKeyword, x, y, 5).exceptionOrNull()
//
//        // then
//        Truth.assertThat(action).isInstanceOf(CustomError.EmptyResults::class.java)
//    }

    companion object {
        private const val x = 37.284
        private const val y = 127.1071
        private val brandKeyword = listOf("스타벅스", "베스킨라빈스", "BHC", "BBQ", "GS25", "CU", "아파트", "어린이집", "파파존스")
        private val brandResult = brandKeyword.map {
            BrandPlaceInfo("서울 중구", "스타벅스", "", "", "", "")
        }
        private val brandPlaceInfoResults: List<BrandPlaceInfo> = listOf(
            BrandPlaceInfo("서울 중구", "스타벅스", "", "", "", "")
        )
    }
}
*/
