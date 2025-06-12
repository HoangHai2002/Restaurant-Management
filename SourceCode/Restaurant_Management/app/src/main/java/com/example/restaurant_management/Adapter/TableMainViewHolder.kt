package com.example.restaurant_management.Adapter

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.R
import org.w3c.dom.Text

class TableMainViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var tvTableName = itemView.findViewById<TextView>(R.id.tvTableName)
    var tvGuestCount = itemView.findViewById<TextView>(R.id.tvGuestCount)
    var tvTotal = itemView.findViewById<TextView>(R.id.tvTotal)
    var layout = itemView.findViewById<ConstraintLayout>(R.id.layoutItemTable)
    var tvNote = itemView.findViewById<TextView>(R.id.tvNote)
}