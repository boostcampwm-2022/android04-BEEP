package com.lighthouse.presentation.ui.widget

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.lighthouse.domain.model.DbResult
import com.lighthouse.domain.usecase.GetBrandPlaceInfosUseCase
import com.lighthouse.domain.usecase.GetGifticonsUseCase
import com.lighthouse.presentation.mapper.toPresentation
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltWorker
@SuppressLint("MissingPermission")
class BeepWidgetWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    getGifticonsUseCase: GetGifticonsUseCase,
    private val getBrandPlaceInfosUseCase: GetBrandPlaceInfosUseCase
) : CoroutineWorker(context, workerParams) {

    private val gifticonsDbResult = getGifticonsUseCase.getUsableGifticons()
        .stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, DbResult.Loading)

    private val allBrands = gifticonsDbResult.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            emit(gifticons.data.map { it.brand.lowercase() }.distinct())
        }
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, emptyList())

    private val gifticonsCount = gifticonsDbResult.transform { gifticons ->
        if (gifticons is DbResult.Success) {
            val gifticonGroup = gifticons.data
                .groupBy { it.brand.lowercase() }
                .map { it.key to it.value.count() }
                .toMap()
            emit(gifticonGroup)
        }
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, emptyMap())

    private var job: Job? = null

    override suspend fun doWork(): Result {
        if (job?.isActive == true || job != null) job?.cancel()
        setWidgetState(WidgetState.Loading)
        return when (hasLocationPermission()) {
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
        LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener { lastLocation ->
            job = CoroutineScope(Dispatchers.IO).launch {
                getNearBrands(lastLocation.longitude, lastLocation.latitude)
            }
        }
    }

    private suspend fun getNearBrands(x: Double, y: Double) {
        getBrandPlaceInfosUseCase(allBrands.value, x, y, SEARCH_SIZE)
            .mapCatching { brandPlaceInfos -> brandPlaceInfos.toPresentation() }
            .onSuccess { brandPlaceInfoUiModel ->
                val nearGifticonBrands = brandPlaceInfoUiModel
                    .map { Pair(it.brand, it.categoryName) }
                    .distinct()
                    .toMap()

                val nearGifticonCount = gifticonsCount.value.filter { gifticon ->
                    nearGifticonBrands[gifticon.key] != null
                }

                val gifticonAndBrandWithCategory = nearGifticonBrands.map { it to nearGifticonCount[it.key] }

                when (nearGifticonCount.isEmpty()) {
                    true -> setWidgetState(WidgetState.Empty)
                    false -> setWidgetState(WidgetState.Available(gifticonAndBrandWithCategory))
                }
            }
            .onFailure { throwable ->
                Timber.tag("TAG").d("${javaClass.simpleName} widget worker throw -> $throwable")
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

    private fun hasLocationPermission() =
        context.hasLocationPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
            context.hasLocationPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

    private fun Context.hasLocationPermission(permission: String): Boolean {
        if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION &&
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
        ) {
            return true
        }

        return ActivityCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val SEARCH_SIZE = 15
    }
}
