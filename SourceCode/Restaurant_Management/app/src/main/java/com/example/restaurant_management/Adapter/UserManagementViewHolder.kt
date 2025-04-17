package com.example.restaurant_management.Adapter

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.R

class UserManagementViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var fullName = itemView.findViewById<TextView>(R.id.tvFullname)
    var role = itemView.findViewById<TextView>(R.id.tvRole)
    var isActive = itemView.findViewById<TextView>(R.id.isActive)
    var imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)
}