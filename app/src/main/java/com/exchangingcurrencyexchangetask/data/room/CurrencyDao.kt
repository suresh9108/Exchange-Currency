package com.exchangingcurrencyexchangetask.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyRate(currencyRate: CurrencyRate)

    @Query("SELECT * FROM currency_rate WHERE baseCurrency = :baseCurrency")
    suspend fun getRatesForBaseCurrency(baseCurrency: String): CurrencyRate?

    @Query("DELETE FROM currency_rate")
    suspend fun deleteAll()
}
