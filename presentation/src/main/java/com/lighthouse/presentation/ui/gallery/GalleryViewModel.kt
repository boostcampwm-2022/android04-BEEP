package com.lighthouse.presentation.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.lighthouse.domain.usecase.GetGalleryImagesUseCase
import com.lighthouse.presentation.mapper.toPresentation
import com.lighthouse.presentation.model.GalleryUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    getGalleryImagesUseCase: GetGalleryImagesUseCase
) : ViewModel() {

    private val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val list = getGalleryImagesUseCase().map { pagingData ->
        pagingData.map {
            it.toPresentation()
        }.insertSeparators { before: GalleryUIModel.Gallery?, after: GalleryUIModel.Gallery? ->
            if (before == null && after != null) {
                GalleryUIModel.Header(format.format(after.date))
            } else if (before != null && after != null) {
                val beforeDate = format.format(before.date)
                val afterDate = format.format(after.date)
                if (beforeDate != afterDate) {
                    GalleryUIModel.Header(afterDate)
                } else {
                    null
                }
            } else {
                null
            }
        }
    }.cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())
}
