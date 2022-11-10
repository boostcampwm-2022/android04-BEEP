package com.lighthouse.presentation.ui.gallery

import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {
//    val projection = arrayOf(
//        MediaStore.Images.Media._ID,
//        MediaStore.Images.Media.DATE_ADDED
//    )
//    val selection = "${MediaStore.Images.Media.MIME_TYPE} in (?,?)"
//    val mimeTypeMap = MimeTypeMap.getSingleton()
//    val selectionArg = arrayOf(
//        mimeTypeMap.getMimeTypeFromExtension("png"),
//        mimeTypeMap.getMimeTypeFromExtension("jpg")
//    )
//    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
//
//    val cursor = contentResolver.query(
//        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//        projection,
//        selection,
//        selectionArg,
//        sortOrder
//    )
//
//    cursor?.use {
//        val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
//        val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
//
//        while (it.moveToNext()) {
//            val id = it.getLong(idColumn)
//            val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
//            val dateAdded = it.getLong(dateAddedColumn)
//            val date = Date(dateAdded)
//        }
//    }
}
