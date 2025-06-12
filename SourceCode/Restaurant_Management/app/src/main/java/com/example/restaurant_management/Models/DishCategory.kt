package com.example.restaurant_management.Models

data class DishCategory(
    var id: String? = "",
    var dishCategoryName: String? = ""
) {
    override fun toString(): String {
        return dishCategoryName!!
    }
}