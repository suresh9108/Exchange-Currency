package com.exchangingcurrencyexchangetask.data.model

data class ExchangeRateResponse(
    val base_code: String, // Add this line
    val conversion_rates: Map<String, Double>
)
