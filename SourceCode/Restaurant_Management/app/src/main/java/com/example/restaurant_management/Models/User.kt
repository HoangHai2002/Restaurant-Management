package com.example.restaurant_management.Models

data class User(
    var id: String? = null,
    var fullName: String? = null,
    var userName: String? = null,
    var passWord: String? = null,
    var role: Int? = null,
    var active: Boolean = true
) {
    fun convertRoleToText(role: Int): String{
        when(role){
            0 -> { return "Admin"}
            1 -> { return "Thu Ngân"}
            2 -> { return "Phục vụ"}
            3 -> { return "Bếp"}
            else -> ""
        }
        return ""
    }
}