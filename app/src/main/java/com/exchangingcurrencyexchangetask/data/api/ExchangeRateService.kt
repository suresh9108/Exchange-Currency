package com.exchangingcurrencyexchangetask.data.api

import com.exchangingcurrencyexchangetask.data.model.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExchangeRateService {
    @GET("latest/{baseCurrency}")
    suspend fun getExchangeRates(
        @Path("baseCurrency") baseCurrency: String): ExchangeRateResponse
}
