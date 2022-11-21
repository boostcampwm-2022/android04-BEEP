package com.lighthouse.presentation.ui.map

import com.google.common.truth.Truth
import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.domain.model.CustomError
import com.lighthouse.domain.usecase.GetBrandPlaceInfosUseCase
import com.lighthouse.presentation.ui.common.UiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.DisplayName

@ExperimentalCoroutinesApi
class MapViewModelTest {

    private val getBrandPlaceInfosUseCase: GetBrandPlaceInfosUseCase = mockk()

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    @DisplayName("[성공] 검색 결과를 갖고오는데 성공하면 MapState는 Success가 된다")
    fun getBrandPlaceInfoSuccess() {
        // given
        coEvery {
            getBrandPlaceInfosUseCase(brandList, "37.284", "127.1071", 5)
        } returns Result.success(brandPlaceInfo)

        // when
        val viewModel = MapViewModel(getBrandPlaceInfosUseCase)
        viewModel.getBrandPlaceInfos(37.2840, 127.1071)
        val actual = viewModel.state.value

        // then
        Truth.assertThat(actual).isInstanceOf(UiState.Success::class.java)
    }

    @Test
    @DisplayName("[실패] 검색 결과가 존재하지 않는다면 MapState는 NotFoundSearchResults가 된다")
    fun getBrandPlaceInfoNotFoundSearchResults() {
        // given
        coEvery {
            getBrandPlaceInfosUseCase(brandList, "37.284", "127.1071", 5)
        } returns Result.failure(CustomError.EmptyResults)

        // when
        val viewModel = MapViewModel(getBrandPlaceInfosUseCase)
        viewModel.getBrandPlaceInfos(37.284, 127.1071)
        val actual = viewModel.state.value

        // then
        Truth.assertThat(actual).isInstanceOf(UiState.NotFoundResults::class.java)
    }

    @Test
    @DisplayName("[실패] 네트워트 연결이 문제가 생긴다면 MapState는 NetworkFailure가 된다.")
    fun getBrandPlaceInfoNetworkError() {
        // given
        coEvery {
            getBrandPlaceInfosUseCase(brandList, "37.284", "127.1071", 5)
        } returns Result.failure(CustomError.NetworkFailure)

        // when
        val viewModel = MapViewModel(getBrandPlaceInfosUseCase)
        viewModel.getBrandPlaceInfos(37.2840, 127.1071)
        val actual = viewModel.state.value

        // then
        Truth.assertThat(actual).isInstanceOf(UiState.NetworkFailure::class.java)
    }

    companion object {
        private val brandList = listOf("스타벅스", "베스킨라빈스", "BHC", "BBQ", "GS25", "CU", "아파트", "어린이집")
        private val brandPlaceInfo: List<BrandPlaceInfo> = listOf(BrandPlaceInfo("서울 중구", "스타벅스", "", "", "", ""))
    }
}
