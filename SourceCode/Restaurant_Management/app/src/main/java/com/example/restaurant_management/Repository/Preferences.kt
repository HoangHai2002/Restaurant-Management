package com.example.restaurant_management.Repository

import android.content.Context

class Preferences(context: Context) {
    val  sharePreferences =context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private val editor =sharePreferences.edit()

    fun  saveLogin(id : String, isAdmin: Boolean){
        editor.putString("id", id)
        editor.putBoolean("isAdmin", isAdmin)
        editor.putBoolean("isLogin", true)
        editor.apply()
    }
    fun isLogin(): Boolean{
        return sharePreferences.getBoolean("isLogin", false)
    }
    fun isAdmin(): Boolean{
        return sharePreferences.getBoolean("isAdmin", false)
    }
    fun getId(): String?{
        return sharePreferences.getString("id", null)
    }
    fun logout(){
        editor.remove("id")
        editor.remove("isAdmin")
        editor.remove("isLogin")
        editor.apply()
    }
}