package com.example.restaurant_management.Models

data class Dish(
    var id: String? = "",
    var idLoaiMonAn: String? = "",
    var tenMonAn: String? = "",
    var gia: Double,
    var trangThai: Boolean
) {
}