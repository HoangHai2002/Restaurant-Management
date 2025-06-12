package com.example.restaurant_management.Adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.R

class HistoryViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
    var tvDishName = itemView.findViewById<TextView>(R.id.tvDishName)
    var tvQuantity = itemView.findViewById<TextView>(R.id.tvQuantity)
    var tvCreatedAt = itemView.findViewById<TextView>(R.id.tvCreatedAt)
    var tvCreatedBy = itemView.findViewById<TextView>(R.id.tvCreatedBy)
}