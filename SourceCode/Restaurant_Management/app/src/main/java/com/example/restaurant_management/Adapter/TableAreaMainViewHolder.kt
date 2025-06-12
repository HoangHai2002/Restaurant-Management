package com.example.restaurant_management.Adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.R

class TableAreaMainViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
    var tvTableAreaName = itemView.findViewById<TextView>(R.id.item_tableAreaName)
}