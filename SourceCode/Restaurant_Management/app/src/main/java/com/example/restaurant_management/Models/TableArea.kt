package com.example.restaurant_management.Models

data class TableArea(
    var id: String? = "",
    var tableAreaName: String? = ""
) {
    override fun toString(): String {
        return tableAreaName!!
    }
}