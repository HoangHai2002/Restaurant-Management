package com.example.restaurant_management.Adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.R
import org.w3c.dom.Text

class KitchenViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
    var tvDishName = itemView.findViewById<TextView>(R.id.tvDishName)
    var tvSL = itemView.findViewById<TextView>(R.id.tvSl)
    var tvTableName = itemView.findViewById<TextView>(R.id.tvTableName)
    var tvTime = itemView.findViewById<TextView>(R.id.tvTime)
    var iconStatus = itemView.findViewById<TextView>(R.id.iconStatus)
    var tvAction = itemView.findViewById<TextView>(R.id.tvAction)
    var tvNote = itemView.findViewById<TextView>(R.id.tvNote)
}