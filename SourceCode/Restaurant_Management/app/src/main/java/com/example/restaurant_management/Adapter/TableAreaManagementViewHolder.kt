package com.example.restaurant_management.Adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.R

class TableAreaManagementViewHolder(itemView : View ): RecyclerView.ViewHolder(itemView) {
    var tableAreaName = itemView.findViewById<TextView>(R.id.tvTenKhuVuc)
    var slBan = itemView.findViewById<TextView>(R.id.tvSlBan)
    var overFlowMenu = itemView.findViewById<ImageView>(R.id.imvOverFlowMenu)
}