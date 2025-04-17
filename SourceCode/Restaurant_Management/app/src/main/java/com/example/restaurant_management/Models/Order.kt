package com.example.restaurant_management.Models

import java.time.LocalDateTime

data class Order(
    var id: String? = "",
    var idBan: String? = "",
    var idMonAn: String? = "",
    var idUser: String? = "",
    var soLuong: Int,
    var thoiGian: LocalDateTime,
    var trangThai: Boolean
) {
}