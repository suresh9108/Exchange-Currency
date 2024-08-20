package com.exchangingcurrencyexchangetask.data.repo

import com.exchangingcurrencyexchangetask.network.NetworkUtils.isInternetAvailable
import javax.inject.Inject
import javax.inject.Singleton

import android.content.Context
import android.widget.Toast
import com.exchangingcurrencyexchangetask.data.room.CurrencyRate
import com.exchangingcurrencyexchangetask.data.api.ExchangeRateService
import com.exchangingcurrencyexchangetask.data.room.CurrencyDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class CurrencyRepository @Inject constructor(
    private val exchangeRateService: ExchangeRateService,
    private val currencyDao: CurrencyDao,
    @ApplicationContext private val context: Context
) {
    suspend fun getRates(baseCurrency: String): Map<String, Double>? {
        if (!isInternetAvailable(context)) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show()
            }
            return null
        }

        return try {
            val response = exchangeRateService.getExchangeRates(baseCurrency)
            val currencyRate = CurrencyRate(
                baseCurrency = response.base_code,
                rates = response.conversion_rates,
                timestamp = System.currentTimeMillis()
            )
            currencyDao.insertCurrencyRate(currencyRate)
            response.conversion_rates
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to fetch rates: ${e.message}", Toast.LENGTH_LONG).show()
            }
            null
        }
    }
    suspend fun getStoredRates(baseCurrency: String): CurrencyRate? {
        return currencyDao.getRatesForBaseCurrency(baseCurrency)
    }
}
