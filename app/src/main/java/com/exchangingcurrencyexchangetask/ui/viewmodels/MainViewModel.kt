package com.exchangingcurrencyexchangetask.ui.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exchangingcurrencyexchangetask.network.NetworkUtils.isInternetAvailable
import com.exchangingcurrencyexchangetask.data.repo.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val application: Application
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _conversionResult = MutableLiveData<String>()
    val conversionResult: LiveData<String> get() = _conversionResult

    private val _currencies = MutableLiveData<List<String>>()
    val currencies: LiveData<List<String>> get() = _currencies

    init {
        _isLoading.value = true // Start loading
        viewModelScope.launch {
            loadCurrencies("USD")
        }
    }

    private suspend fun loadCurrencies(baseCurrency: String) {
        val rates = if (isInternetAvailable(application)) {
            repository.getRates(baseCurrency)
        } else {
            val currencyRate = repository.getStoredRates(baseCurrency)
            currencyRate?.rates ?: emptyMap()
        }

        if (rates?.isNotEmpty() == true) {
            _currencies.value = rates?.keys?.toList()
        } else {
            _currencies.value = emptyList()
        }
        _isLoading.value = false // Stop loading after currencies are loaded
    }

    fun convert(amount: Double, from: String, to: String) {
        _isLoading.value = true // Start loading during conversion
        viewModelScope.launch {
            val rates = if (isInternetAvailable(application)) {
                repository.getRates(to)
            } else {
                val currencyRate = repository.getStoredRates("USD")
                currencyRate?.rates ?: emptyMap()
            }

            if (rates?.isNotEmpty() == true) {
                val fromRate = rates[from] ?: 1.0
                val toRate = rates[to] ?: 1.0
                val result = (amount / fromRate) * toRate
                _conversionResult.value = result.toString()
            } else {
                _conversionResult.value = "Rates not available"
            }

            _isLoading.value = false // Stop loading after conversion is done
        }
    }
}

