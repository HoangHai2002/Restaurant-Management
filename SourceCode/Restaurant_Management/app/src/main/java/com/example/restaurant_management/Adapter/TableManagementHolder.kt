package com.example.restaurant_management.Adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.R

class TableManagementHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    var iconStatus = itemView.findViewById<TextView>(R.id.iconStatus)
    var tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
    var tvTableName = itemView.findViewById<TextView>(R.id.tvTableName)
    var tvTableArea = itemView.findViewById<TextView>(R.id.tvTableAreaName)
    var menu = itemView.findViewById<ImageView>(R.id.menu)
}