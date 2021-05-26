package com.myetherwallet.mewwalletkit.core.extension

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

fun BigDecimal?.formatCurrency(currencyName: String, decimals: Int = 2, onError: () -> String) = formatCurrency(
    NumberFormat.getCurrencyInstance() as DecimalFormat, currencyName, decimals, onError)

fun BigDecimal?.formatCurrency(format: DecimalFormat, currencyName: String, decimals: Int = 2, onError: () -> String) =
    this?.let {
        try {
            format.maximumFractionDigits = decimals
            format.currency = Currency.getInstance(currencyName)
            format.format(this)
        } catch (e: Exception) {
            onError()
        }
    } ?: onError()
