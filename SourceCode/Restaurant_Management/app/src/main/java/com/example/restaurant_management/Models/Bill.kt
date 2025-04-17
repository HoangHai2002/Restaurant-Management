package com.example.restaurant_management.Models

import java.time.LocalDateTime

data class Bill(
    var id: String? = "",
    var idBan: String? = "",
    var thoiGian: LocalDateTime,
    var order: Order
) {
}