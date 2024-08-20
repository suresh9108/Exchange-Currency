package com.exchangingcurrencyexchangetask.data.room

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey

@Entity(tableName = "currency_rate")
data class  CurrencyRate(
    @PrimaryKey val baseCurrency: String,
    val rates: Map<String, Double>,
    val timestamp: Long
)

