package com.lighthouse.data.content.datasource

import android.content.ContentResolver
import android.content.ContentUris
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.lighthouse.beep.model.gallery.GalleryImage
import com.lighthouse.common.mapper.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

internal class GalleryImageDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) {

    suspend fun getImages(page: Int, limit: Int): List<GalleryImage> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED
        )
        val selection = "${MediaStore.Images.Media.MIME_TYPE} in (?,?)"
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val selectionArg = arrayOf(
            mimeTypeMap.getMimeTypeFromExtension("png"),
            mimeTypeMap.getMimeTypeFromExtension("jpg")
        )
        val offset = page * limit
        val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val queryArgs = Bundle().apply {
                putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArg)
                putStringArray(
                    ContentResolver.QUERY_ARG_SORT_COLUMNS,
                    arrayOf(MediaStore.Images.Media.DATE_ADDED)
                )
                putInt(
                    ContentResolver.QUERY_ARG_SORT_DIRECTION,
                    ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                )
                putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
            }
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                queryArgs,
                null
            )
        } else {
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT $limit OFFSET $offset"
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArg,
                sortOrder
            )
        }

        val list = ArrayList<GalleryImage>()
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val dateAdded = it.getLong(dateAddedColumn)
                val date = Date(dateAdded * 1000)
                list.add(GalleryImage(id, contentUri.toDomain(), date))
            }
        }
        return@withContext list
    }
}
