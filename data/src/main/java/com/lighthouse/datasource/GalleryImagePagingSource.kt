package com.lighthouse.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lighthouse.domain.model.GalleryImage

class GalleryImagePagingSource(
    private val localSource: GalleryImageLocalSource,
    private val page: Int,
    private val limit: Int
) : PagingSource<Int, GalleryImage>() {

    override fun getRefreshKey(state: PagingState<Int, GalleryImage>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryImage> {
        val current = params.key ?: page
        val results = localSource.getImages(current, limit)
        return try {
            LoadResult.Page(
                data = results,
                prevKey = null,
                nextKey = if (results.size < params.loadSize) null else current + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
