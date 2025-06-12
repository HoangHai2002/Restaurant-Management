package com.example.restaurant_management.Adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.R
import org.w3c.dom.Text

class DishCategoryViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
    var tvDishCategoryName = itemView.findViewById<TextView>(R.id.tvDishCategoryName)
    var imvMenu = itemView.findViewById<ImageView>(R.id.imvMenu)
}
