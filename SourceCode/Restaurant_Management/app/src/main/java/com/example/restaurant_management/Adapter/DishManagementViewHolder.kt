package com.example.restaurant_management.Adapter

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.R

class DishManagementViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
    var tvDishName = itemView.findViewById<TextView>(R.id.tvDishName)
    var tvDishCategoryName = itemView.findViewById<TextView>(R.id.tvDishCategoryName)
    var tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
    var imvMenu = itemView.findViewById<ImageView>(R.id.imvMenu)
    var imvTru = itemView.findViewById<ImageView>(R.id.imvTru)
    var imvCong = itemView.findViewById<ImageView>(R.id.imvCong)
    var edtQuantity = itemView.findViewById<EditText>(R.id.edtQuantity)
}