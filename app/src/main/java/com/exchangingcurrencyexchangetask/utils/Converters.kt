package com.exchangingcurrencyexchangetask.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
class Converters {
    @TypeConverter
    fun fromMap(value: Map<String, Double>?): String? {
        return value?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toMap(value: String?): Map<String, Double>? {
        return value?.let {
            val mapType = object : TypeToken<Map<String, Double>>() {}.type
            Gson().fromJson(it, mapType)
        }
    }
}
