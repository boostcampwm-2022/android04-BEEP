package com.lighthouse.datasource.gallery

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lighthouse.domain.model.GalleryImage

class GalleryImagePagingSource(
    private val localSource: GalleryImageLocalSource,
    private val page: Int,
    private val limit: Int
) : PagingSource<Int, GalleryImage>() {

    override fun getRefreshKey(state: PagingState<Int, GalleryImage>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryImage> {
        val current = params.key ?: page
        val results = localSource.getImages(current, params.loadSize)
        return try {
            LoadResult.Page(
                data = results,
                prevKey = null,
                nextKey = if (results.size < params.loadSize) null else current + (params.loadSize / limit)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
