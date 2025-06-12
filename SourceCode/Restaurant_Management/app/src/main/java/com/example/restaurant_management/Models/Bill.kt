package com.example.restaurant_management.Models

import java.time.LocalDateTime

data class Bill(
    var id: String? = "",
    var discount : Int = 0,
    var total : Double = 0.0,
    var discountType : String? = "",
    var paymentMethod : String? = "",
    var createdAt: String? = "",
    var createdBy: String? = "",
    var order: Order? = null
) {
}