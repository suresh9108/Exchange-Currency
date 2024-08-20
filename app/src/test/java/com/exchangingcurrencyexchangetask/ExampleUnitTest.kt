import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.exchangingcurrencyexchangetask.data.repo.CurrencyRepository
import com.exchangingcurrencyexchangetask.data.room.CurrencyRate
import com.exchangingcurrencyexchangetask.ui.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class ExampleUnitTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MainViewModel
    private lateinit var repository: CurrencyRepository
    private lateinit var conversionResultObserver: Observer<String>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher) // Set the main dispatcher to test dispatcher
        repository = mock(CurrencyRepository::class.java)
        viewModel = MainViewModel(repository, mock(Application::class.java))

        // Set up the observer for conversion result
        conversionResultObserver = mock()
        viewModel.conversionResult.observeForever(conversionResultObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the main dispatcher to the original Main dispatcher
        viewModel.conversionResult.removeObserver(conversionResultObserver)
    }

    @Test
    fun `test conversion result`() = runTest {
        val rates = mapOf("USD" to 1.0, "EUR" to 0.85)
        val currencyRate = CurrencyRate("USD", rates, System.currentTimeMillis())

        `when`(repository.getStoredRates("USD")).thenReturn(currencyRate)

        // Launch coroutine for conversion
        viewModel.convert(100.0, "USD", "EUR")

        advanceUntilIdle() // Advance the dispatcher until all coroutines are completed

        // Verify the result
        verify(conversionResultObserver).onChanged("85.0")
    }
}
