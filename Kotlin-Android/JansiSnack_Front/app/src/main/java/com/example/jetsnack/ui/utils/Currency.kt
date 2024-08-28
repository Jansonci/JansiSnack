package com.example.jetsnack.ui.utils

import java.math.BigDecimal
import java.text.NumberFormat

fun formatPrice(price: Double): String {
    return NumberFormat.getCurrencyInstance().format(
        BigDecimal(price).movePointLeft(2)
    )
}
fun formatPrice(price: Long): String {
    return NumberFormat.getCurrencyInstance().format(
        BigDecimal(price).movePointLeft(2)
    )
}