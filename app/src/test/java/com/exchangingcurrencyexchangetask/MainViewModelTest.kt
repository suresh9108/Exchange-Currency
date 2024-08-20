package com.exchangingcurrencyexchangetask

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.exchangingcurrencyexchangetask.data.repo.CurrencyRepository
import com.exchangingcurrencyexchangetask.ui.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    @Mock
    private lateinit var repository: CurrencyRepository

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var isLoadingObserver: Observer<Boolean>

    @Mock
    private lateinit var conversionResultObserver: Observer<String>

    @Mock
    private lateinit var currenciesObserver: Observer<List<String>>

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(repository, application)
        viewModel.isLoading.observeForever(isLoadingObserver)
        viewModel.conversionResult.observeForever(conversionResultObserver)
        viewModel.currencies.observeForever(currenciesObserver)
    }

    @Test
    fun `test successful currency load with internet`() = runTest {
        val rates = mapOf("USD" to 1.0, "EUR" to 0.85)
        `when`(repository.getRates("USD")).thenReturn(rates)

        viewModel.loadCurrencies("USD")

        advanceUntilIdle() // Ensure all coroutines have completed

        verify(isLoadingObserver).onChanged(true)
        verify(currenciesObserver).onChanged(listOf("USD", "EUR"))
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test failed currency load without internet`() = runTest {
        `when`(repository.getRates("USD")).thenReturn(null)
        `when`(repository.getStoredRates("USD")).thenReturn(null)

        viewModel.loadCurrencies("USD")

        advanceUntilIdle() // Ensure all coroutines have completed

        verify(isLoadingObserver).onChanged(true)
        verify(currenciesObserver).onChanged(emptyList())
        verify(isLoadingObserver).onChanged(false)
    }

    @Test
    fun `test successful conversion`() = runTest {
        val rates = mapOf("USD" to 1.0, "EUR" to 0.85)
        `when`(repository.getRates("EUR")).thenReturn(rates)

        viewModel.convert(100.0, "USD", "EUR")

        advanceUntilIdle() // Ensure all coroutines have completed

        verify(isLoadingObserver).onChanged(true)
        verify(conversionResultObserver).onChanged("85.0")
        verify(isLoadingObserver).onChanged(false)
    }
}
