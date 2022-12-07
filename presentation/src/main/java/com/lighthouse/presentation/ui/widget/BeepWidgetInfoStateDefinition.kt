package com.lighthouse.presentation.ui.widget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object BeepWidgetInfoStateDefinition : GlanceStateDefinition<WidgetState> {

    private const val DATA_STORE_FILENAME = "beep"

    private val Context.datastore by dataStore(DATA_STORE_FILENAME, GifticonInfoSerializer)

    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<WidgetState> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }

    object GifticonInfoSerializer : Serializer<WidgetState> {

        override val defaultValue = WidgetState.Unavailable(message = "찾을 수 없습니다.")

        override suspend fun readFrom(input: InputStream): WidgetState {
            return Json.decodeFromString(
                WidgetState.serializer(),
                input.readBytes().decodeToString()
            )
        }

        override suspend fun writeTo(t: WidgetState, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(WidgetState.serializer(), t).encodeToByteArray()
                )
            }
        }
    }
}
