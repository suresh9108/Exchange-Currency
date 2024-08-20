package com.exchangingcurrencyexchangetask.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
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

        // Set up Data Binding
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupObservables(binding)
        setupClickListeners(binding)
    }

    private fun setupClickListeners(binding: ActivityMainBinding) {
        binding.buttonConvert.setOnClickListener {
            val amountText = binding.editTextAmount.text.toString()
            val fromCurrency = binding.spinnerFrom.selectedItem?.toString()
            val toCurrency = binding.spinnerTo.selectedItem?.toString()

            // Clear previous error messages
            binding.editTextAmount.error = null
            binding.textViewResult.text = ""

            // Log spinner values for debugging
            Log.d("MainActivity", "From Currency: $fromCurrency")
            Log.d("MainActivity", "To Currency: $toCurrency")

            // Sequential Error Handling
            when {
                fromCurrency == getString(R.string.select_from_currency) -> {
                    binding.textViewResult.setTextColor(getColor(R.color.red))
                    binding.textViewResult.text = getString(R.string.error_select_from_currency)
                }
                toCurrency == getString(R.string.select_to_currency) -> {
                    binding.textViewResult.setTextColor(getColor(R.color.red))
                    binding.textViewResult.text = getString(R.string.error_select_to_currency)
                }
                amountText.isEmpty() -> {
                    binding.textViewResult.setTextColor(getColor(R.color.red))
                    binding.textViewResult.text = getString(R.string.error_empty_amount)
                }
                amountText.toDoubleOrNull() == null -> {
                    binding.textViewResult.setTextColor(getColor(R.color.red))
                    binding.textViewResult.text = getString(R.string.error_invalid_amount)
                }
                fromCurrency == null || toCurrency == null -> {
                    binding.textViewResult.setTextColor(getColor(R.color.red))
                    binding.textViewResult.text = "Error: Unable to determine currency"
                }
                else -> {
                    // No errors, reset to default text color and proceed with the conversion
                    binding.textViewResult.setTextColor(getColor(R.color.black))
                    viewModel.convert(amountText.toDouble(), fromCurrency, toCurrency)
                }
            }
        }
    }



    private fun setupObservables(binding: ActivityMainBinding) {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.currencies.observe(this) { currencies ->
            Log.d("MainActivity", "Currencies: $currencies")
            if (!currencies.isNullOrEmpty()) {
                val fromCurrencies = listOf(getString(R.string.select_from_currency)) + currencies
                val toCurrencies = listOf(getString(R.string.select_to_currency)) + currencies

                val fromAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fromCurrencies)
                val toAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, toCurrencies)

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

        viewModel.conversionResult.observe(this) { result ->
            binding.textViewResult.text = result
        }
    }
}
