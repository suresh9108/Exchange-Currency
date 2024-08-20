package com.exchangingcurrencyexchangetask.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.exchangingcurrencyexchangetask.R
import com.exchangingcurrencyexchangetask.databinding.ActivityMainBinding
import com.exchangingcurrencyexchangetask.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.currencies.observe(this) { currencies ->
            Log.d("MainActivity", "Currencies: $currencies")
            if (currencies != null && currencies.isNotEmpty()) {
                val fromCurrencies = listOf(getString(R.string.select_from_currency)) + currencies
                val toCurrencies = listOf(getString(R.string.select_to_currency)) + currencies

                val fromAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, fromCurrencies)
                val toAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, toCurrencies)

                fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                binding.spinnerFrom.adapter = fromAdapter
                binding.spinnerTo.adapter = toAdapter
            } else {
                Log.e("MainActivity", "Currencies list is empty or null")
                binding.spinnerFrom.adapter = null
                binding.spinnerTo.adapter = null
            }
        }
        binding.buttonConvert.setOnClickListener {
            val amountText = binding.editTextAmount.text.toString()
            val fromCurrency = binding.spinnerFrom.selectedItem.toString()
            val toCurrency = binding.spinnerTo.selectedItem.toString()

            // Clear previous error messages
            binding.editTextAmount.error = null
            binding.textViewResult.text = ""

            // Sequential Error Handling
            when {

                fromCurrency == getString(R.string.select_from_currency) -> {
                    binding.textViewResult.text = getString(R.string.error_select_from_currency)
                }

                toCurrency == getString(R.string.select_to_currency) -> {
                    binding.textViewResult.text = getString(R.string.error_select_to_currency)
                }

                amountText.isEmpty() -> {
                    binding.editTextAmount.error = getString(R.string.error_empty_amount)
                }

                amountText.toDoubleOrNull() == null -> {
                    binding.editTextAmount.error = getString(R.string.error_invalid_amount)
                }

                else -> {
                    // No errors, proceed with the conversion
                    viewModel.convert(amountText.toDouble(), fromCurrency, toCurrency)
                }
            }
        }



        viewModel.conversionResult.observe(this) { result ->
            binding.textViewResult.text = result
        }
    }
}
