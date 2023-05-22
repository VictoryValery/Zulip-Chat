package com.victoryvalery.tfsproject.data.roomStorage.mappers

import androidx.room.TypeConverter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

open class ListJsonConverter<T>(private val kSerializer: KSerializer<List<T>>) {
    @TypeConverter
    fun fromList(data: List<T>): String {
        return Json.encodeToString(kSerializer, data)
    }

    @TypeConverter
    fun toList(data: String): List<T> {
        return Json.decodeFromString(kSerializer, data)
    }
}
