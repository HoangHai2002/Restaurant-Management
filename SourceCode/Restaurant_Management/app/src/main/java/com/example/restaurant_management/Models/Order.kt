package com.example.restaurant_management.Models

import java.time.LocalDateTime

data class Order(
    var id: String? = "",
    var tableId: String? = "",
    var tableName: String? = "",
    var total: Double = 0.0,
    var status: Boolean = true,
    var note: String? = "",
    var guest: Int = 0,
    var createAt: String? = "",
    var orderItem : MutableList<OrderItem> = mutableListOf()
) {
}