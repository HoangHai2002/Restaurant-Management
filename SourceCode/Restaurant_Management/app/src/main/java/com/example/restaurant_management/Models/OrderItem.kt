package com.example.restaurant_management.Models

import java.util.Date

data class OrderItem(
    var orderItemId: String? = "",
    var orderId: String? = "",
    var dishId: String? = "",
    var dishName: String? = "",
    var tableName: String? = "",
    var quantity: Int = 0,
    var price: Double = 0.0,
    var note: String? = "",
    var orderBy: String? = "",
    var orderAt: String? = "",
    var status: String? = ""

)
