/*
package com.lighthouse.presentation.ui.map

import com.lighthouse.domain.model.BrandPlaceInfo
import com.lighthouse.domain.usecase.GetBrandPlaceInfosUseCase
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

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

//    @Test
//    @DisplayName("[성공] 검색 결과를 갖고오는데 성공하면 MapState는 Success가 된다")
//    fun getBrandPlaceInfoSuccess() = runTest {
//        // given
//        coEvery {
//            getBrandPlaceInfosUseCase(brandList, x, y, 5)
//        } returns Result.success(brandPlaceInfo)
//
//        // when
//        val viewModel = MapViewModel(getBrandPlaceInfosUseCase)
//
//        // then
//        viewModel.state.test {
//            viewModel.getBrandPlaceInfos(x, y)
//            val actual = awaitItem()
//            Truth.assertThat(actual).isInstanceOf(UiState.Success::class.java)
//        }
//    }
//
//    @Test
//    @DisplayName("[실패] 검색 결과가 존재하지 않는다면 MapState는 NotFoundSearchResults가 된다")
//    fun getBrandPlaceInfoNotFoundSearchResults() = runTest {
//        // given
//        coEvery {
//            getBrandPlaceInfosUseCase(brandList, x, y, 5)
//        } returns Result.failure(CustomError.EmptyResults)
//
//        // when
//        val viewModel = MapViewModel(getBrandPlaceInfosUseCase)
//
//        // then
//        viewModel.state.test {
//            viewModel.getBrandPlaceInfos(x, y)
//            val actual = awaitItem()
//            Truth.assertThat(actual).isInstanceOf(UiState.NotFoundResults::class.java)
//        }
//    }
//
//    @Test
//    @DisplayName("[실패] 네트워크 연결이 문제가 생긴다면 MapState는 NetworkFailure가 된다.")
//    fun getBrandPlaceInfoNetworkError() = runTest {
//        // given
//        coEvery {
//            getBrandPlaceInfosUseCase(brandList, x, y, 5)
//        } returns Result.failure(CustomError.NetworkFailure)
//
//        // when
//        val viewModel = MapViewModel(getBrandPlaceInfosUseCase)
//        viewModel.state.test {
//            viewModel.getBrandPlaceInfos(x, y)
//            val actual = awaitItem()
//            Truth.assertThat(actual).isInstanceOf(UiState.NetworkFailure::class.java)
//        }
//    }

    companion object {
        private const val x = 37.284
        private const val y = 127.1071

        private val brandList = listOf("스타벅스", "베스킨라빈스", "BHC", "BBQ", "GS25", "CU", "서브웨이", "세븐일레븐", "파파존스")
        private val brandPlaceInfo: List<BrandPlaceInfo> = listOf(BrandPlaceInfo("서울 중구", "스타벅스", "", "", "", ""))
    }
}
*/
