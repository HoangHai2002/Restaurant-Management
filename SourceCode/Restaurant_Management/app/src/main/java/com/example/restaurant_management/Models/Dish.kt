package com.example.restaurant_management.Models

data class Dish(
    var id: String? = "",
    var dishCategoryId: String? = "",
    var dishName: String? = "",
    var dishCategoryName: String? = "",
    var description: String? = "",
    var price: Double = 0.0,
    var status: Boolean = true
) {
}