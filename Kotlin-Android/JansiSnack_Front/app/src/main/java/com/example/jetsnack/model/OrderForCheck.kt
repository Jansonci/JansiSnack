package com.example.jetsnack.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.math.BigDecimal
import java.time.OffsetDateTime

data class OrderForCheck(
    val orderId: Long,
    val orderItems: List<OrderItemEntry>,  // 修改此处为列表
    val payTime: OffsetDateTime,           // 注意这里使用OffsetDateTime而不是OffsetTime
    val stillNeeds: Long,
    val amount: BigDecimal,
    val address: String
)

data class OrderItemEntry(
    val dessert: Dessert,  // 假设Dessert是一个已定义的数据类
    val quantity: Int
)

@RequiresApi(Build.VERSION_CODES.O)
val orderForCheck: OrderForCheck = OrderForCheck(1, listOf(
    OrderItemEntry(
        Dessert(1,"Cupcake","","","",1.00,"",
    1,1,"","","",1,1.00,"https://source.unsplash.com/3U2V5WqK1PQ","", listOf()), 3)
), OffsetDateTime.parse("2024-05-11T16:03:37+08:00"), 2, 20.00.toBigDecimal(),"")