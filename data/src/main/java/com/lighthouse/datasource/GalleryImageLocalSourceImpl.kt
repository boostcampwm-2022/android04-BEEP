package com.lighthouse.datasource

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.ContentResolverCompat
import com.lighthouse.domain.model.GalleryImage
import java.util.Date
import javax.inject.Inject

class GalleryImageLocalSourceImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : GalleryImageLocalSource {

    override suspend fun getImages(): List<GalleryImage> {
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
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArg,
            sortOrder
        )

        val list = ArrayList<GalleryImage>()
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val dateAdded = it.getLong(dateAddedColumn)
                val date = Date(dateAdded)
                list.add(GalleryImage(id, contentUri.toString(), date))
            }
        }
        return list
    }
}
