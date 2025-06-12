package com.example.restaurant_management.Adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.R

class BillViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
    var tvCreatedAt = itemView.findViewById<TextView>(R.id.tvCreatedAt)
    var tvTotal = itemView.findViewById<TextView>(R.id.tvTotal)
    var tvTableName = itemView.findViewById<TextView>(R.id.tvTableName)
    var tvPaymentType = itemView.findViewById<TextView>(R.id.tvPaymentType)
}