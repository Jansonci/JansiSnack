/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetsnack.ui.home.Cart

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetsnack.api.DessertService
import com.example.jetsnack.api.UpdateCartRequest
import com.example.jetsnack.model.CartDetail
import com.example.jetsnack.model.OrderForCheck
import com.example.jetsnack.ui.home.Profile.loggedInUser
import kotlinx.coroutines.launch
import java.math.BigDecimal

/**
 * Holds the contents of the cart and allows changes to it.
 *
 * TODO: Move data to Repository so it can be displayed and changed consistently throughout the app.
 */
@RequiresApi(Build.VERSION_CODES.O)
class CartViewModel(
    private val orderId: Long,
) : ViewModel() {

    private val _cartDetails = mutableStateOf<List<CartDetail>?>(null)
    val cartDetails: State<List<CartDetail>?> = _cartDetails

    private val _order = mutableStateOf<OrderForCheck?>(null)
    val order : State<OrderForCheck?> = _order

    val address: State<String?> = derivedStateOf {
        order.value?.address
    }

    val orderLines: State<List<CartDetail>?> = derivedStateOf {
        // 首先检查cartDetails是否不为空
        if (cartDetails.value?.isNotEmpty() == true) {
            // 如果不为空，则直接使用cartDetails
            cartDetails.value
        } else {
            // 否则，从order.value中提取并转换数据
            order.value?.orderItems?.map { orderItemEntry ->
                CartDetail(
                    orderItemEntry.dessert,
                    orderItemEntry.quantity
                )
            }
        }
    }

    val subtotal = derivedStateOf {
        calculateSubtotal(cartDetails.value?.takeUnless { it.isEmpty() } ?: order.value?.orderItems
            ?.map { orderItemEntry -> CartDetail(orderItemEntry.dessert, orderItemEntry.quantity) }?: emptyList())
    }
    val shippingCost = derivedStateOf {
        cartDetails.value?.takeUnless { it.isEmpty() }?.size?.times(0.3)?.toBigDecimal()
            ?: (order.value?.amount?.minus(subtotal.value.toBigDecimal()))?:BigDecimal(0)
    }

    val stillNeeds = derivedStateOf {
        order.value?.stillNeeds?:0
    }

    init {
        refreshData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshData(){
        viewModelScope.launch {
            try {
                val cartDetailResult = DessertService.create().getCartt(loggedInUser)
                val orderResult = DessertService.create().getOrders(loggedInUser)
                if (orderResult.isSuccessful) {
                    _order.value = orderResult.body()?.firstOrNull() { it.orderId == orderId }

                }
                if (cartDetailResult.isSuccessful) {
                    _cartDetails.value = cartDetailResult.body()?.toList()
                }
            } catch (e: Exception) {
                // 处理网络异常等
                Log.e("Network error", e.toString())
            }
        }
    }

    fun increaseSnackCount(snackId: Long) {
        val currentCount = _cartDetails.value?.first { it.dessert.id == snackId }?.amount
        viewModelScope.launch {
            DessertService.create().updateCartRequest(UpdateCartRequest(loggedInUser, snackId, 1))
            if (currentCount != null) {
                updateSnackCount(snackId, currentCount + 1)
            }
        }
    }

    fun decreaseSnackCount(snackId: Long) {
        val currentCount = _cartDetails.value?.first { it.dessert.id == snackId }?.amount
        viewModelScope.launch {
            DessertService.create().updateCartRequest(UpdateCartRequest(loggedInUser, snackId, 0))
            if (currentCount == 1) {
                // remove snack from cart
                removeSnack(snackId)
            } else {
                // update quantity in cart
                if (currentCount != null) {
                    updateSnackCount(snackId, currentCount - 1)
                }
            }
        }
    }

    fun removeSnack(snackId: Long) {
        viewModelScope.launch {
            DessertService.create().updateCartRequest(UpdateCartRequest(loggedInUser, snackId, -1))
            _cartDetails.value = _cartDetails.value?.filter { it.dessert.id != snackId }
        }
    }

    private fun updateSnackCount(snackId: Long, count: Int) {
        _cartDetails.value = _cartDetails.value?.map {
            if (it.dessert.id == snackId) {
                it.copy(amount = count)
            } else {
                it
            }
        }
    }

    /**
     * Factory for CartViewModel that takes SnackbarManager as a dependency
     */
    companion object {
        fun provideFactory(
            orderId:Long = -1,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CartViewModel(orderId) as T
            }
        }
    }
}

fun calculateSubtotal(cartDetails: List<CartDetail>): Double{
    var result = 0.0
    for (cartDetail in cartDetails){
        result += cartDetail.dessert.price.times(cartDetail.amount)
    }
    return result
}
