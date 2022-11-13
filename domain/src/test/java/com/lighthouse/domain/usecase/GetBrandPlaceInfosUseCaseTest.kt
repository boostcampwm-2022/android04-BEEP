package com.lighthouse.domain.usecase

import com.google.common.truth.Truth
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.domain.model.CustomError
import com.lighthouse.domain.repository.BrandRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.DisplayName

@ExperimentalCoroutinesApi
class GetBrandPlaceInfosUseCaseTest {

    private val brandRepository: BrandRepository = mockk()

    @Test
    @DisplayName("[성공] 검색 결과를 갖고온다")
    fun getBrandPlaceInfoSuccess() = runTest {
        // given
        val useCase = GetBrandPlaceInfosUseCase(brandRepository)
        for (keyword in brandKeyword) {
            coEvery {
                brandRepository.getBrandPlaceInfo(keyword, "37.2840", "127.1071", "500", 5)
            } returns Result.success(brandPlaceInfo)
        }

        // when
        val action = useCase(brandKeyword, "37.2840", "127.1071", "500", 5).getOrThrow()

        // then
        Truth.assertThat(action).isEqualTo(brandPlaceInfoResults)
    }

    @Test
    @DisplayName("[실패] 검색 결과가 존재하지 않는다")
    fun getBrandPlaceInfoNotFound() = runTest {
        // given
        val useCase = GetBrandPlaceInfosUseCase(brandRepository)
        for (keyword in brandKeyword) {
            coEvery {
                brandRepository.getBrandPlaceInfo(keyword, "37.2840", "127.1071", "500", 5)
            } returns Result.success(emptyList())
        }

        // when
        val action = useCase(brandKeyword, "37.2840", "127.1071", "500", 5).exceptionOrNull()

        // then
        Truth.assertThat(action).isInstanceOf(CustomError.NotFoundBrandPlaceInfos::class.java)
    }

    companion object {
        private val brandKeyword: List<String> = listOf("스타벅스", "베스킨라빈스", "BBQ")
        private val brandPlaceInfo: List<BrandPlaceInfo> = listOf(BrandPlaceInfo("서울 중구", "스타벅스", "", "", "", ""))
        private val brandPlaceInfoResults: List<BrandPlaceInfo> = listOf(
            BrandPlaceInfo("서울 중구", "스타벅스", "", "", "", ""),
            BrandPlaceInfo("서울 중구", "스타벅스", "", "", "", ""),
            BrandPlaceInfo("서울 중구", "스타벅스", "", "", "", "")
        )
    }
}
