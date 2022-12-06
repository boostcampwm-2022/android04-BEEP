package com.lighthouse.presentation.ui.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.usecase.GetBrandPlaceInfosUseCase
import com.lighthouse.domain.usecase.GetGifticonsUseCase
import com.lighthouse.domain.usecase.GetUserLocationUseCase
import com.lighthouse.domain.usecase.HasLocationPermissionsUseCase
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.mapper.toWidgetModel
import com.lighthouse.presentation.util.LocationCalculateService.diffLocation
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext

@HiltWorker
class BeepWidgetWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    hasLocationPermissionsUseCase: HasLocationPermissionsUseCase,
    getGifticonsUseCase: GetGifticonsUseCase,
    private val getBrandPlaceInfosUseCase: GetBrandPlaceInfosUseCase,
    private val getUserLocationUseCase: GetUserLocationUseCase
) : CoroutineWorker(context, workerParams) {

    private val hasLocationPermission = hasLocationPermissionsUseCase()

    private val gifticonsDbResult = getGifticonsUseCase.getUsableGifticons()
        .stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, DbResult.Loading)

    private val allBrands = gifticonsDbResult.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            emit(gifticons.data.map { it.brand }.distinct())
        }
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, emptyList())

    private val gifticons = gifticonsDbResult.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            val gifticonGroup = gifticons.data.groupBy { it.brand }
            emit(gifticonGroup)
        }
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, emptyMap())

    override suspend fun doWork(): Result {
        return when (hasLocationPermission.value) {
            true -> {
                startWidget()
                Result.success()
            }
            false -> {
                setWidgetState(WidgetState.NoExistsLocationPermission)
                Result.failure()
            }
        }
    }

    private suspend fun startWidget() {
        val lastLocation = getUserLocationUseCase().first()
        getNearBrands(lastLocation.longitude, lastLocation.latitude)
    }

    private suspend fun getNearBrands(x: Double, y: Double) = withContext(Dispatchers.IO) {
        setWidgetState(WidgetState.Loading)
        runCatching { getBrandPlaceInfosUseCase(allBrands.value, x, y, SEARCH_SIZE) }
            .mapCatching { brandPlaceInfos -> brandPlaceInfos.toPresentation() }
            .onSuccess { brandPlaceInfoUiModel ->
                val nearGifticons = brandPlaceInfoUiModel.distinctBy { it.brand }.mapNotNull { info ->
                    gifticons.value[info.brand]?.firstOrNull()
                        ?.toWidgetModel(diffLocation(info, x, y), info.categoryName)
                }

                when (nearGifticons.isEmpty()) {
                    true -> setWidgetState(WidgetState.Empty)
                    false -> setWidgetState(WidgetState.Available(nearGifticons))
                }
            }
            .onFailure { throwable ->
                setWidgetState(WidgetState.Unavailable(throwable.message.orEmpty()))
            }
    }

    private suspend fun setWidgetState(state: WidgetState) {
        val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(BeepWidget::class.java)
        glanceIds.forEach { id ->
            updateAppWidgetState(
                context = context,
                definition = BeepWidgetInfoStateDefinition,
                glanceId = id,
                updateState = { state }
            )
        }
        BeepWidget().updateAll(context)
    }

    companion object {
        private const val SEARCH_SIZE = 15
    }
}
