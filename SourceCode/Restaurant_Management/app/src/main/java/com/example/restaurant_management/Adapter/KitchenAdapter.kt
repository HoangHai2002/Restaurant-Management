package com.example.restaurant_management.Adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.Models.OrderItem
import com.example.restaurant_management.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface IClickDishKitchen{
    fun onClickStart(orderItem: OrderItem, position: Int)
    fun onClickSuccess(orderItem: OrderItem, position: Int)
}
class KitchenAdapter(var list: MutableList<OrderItem>, var listener : IClickDishKitchen): RecyclerView.Adapter<KitchenViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitchenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_orderitem_bep, parent, false)
        return KitchenViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: KitchenViewHolder, position: Int) {
        val itemData = list.get(position)
        holder.tvDishName.setText(itemData.dishName)
        holder.tvNote.setText(itemData.note)
        if(itemData.note == "") holder.tvNote.isVisible = false
        holder.tvSL.setText("SL: "+itemData.quantity.toString())

        val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val dateTime = LocalDateTime.parse(itemData.orderAt, inputFormatter).format(outputFormatter).toString()

        holder.tvTime.setText(dateTime)
        holder.tvTableName.setText(itemData.tableName)
        if (itemData.status == "Chưa làm"){
            holder.iconStatus.setBackgroundResource(R.drawable.circular_orange)
            holder.tvAction.setText("Bắt đầu")
            holder.tvAction.visibility = View.VISIBLE

            holder.tvAction.setOnClickListener {
                listener.onClickStart(itemData, position)
            }
        }
        if (itemData.status == "Đang làm"){
            holder.iconStatus.setBackgroundResource(R.drawable.circular_blue)
            holder.tvAction.setText("Hoàn thành")
            holder.tvAction.visibility = View.VISIBLE

            holder.tvAction.setOnClickListener {
                listener.onClickSuccess(itemData, position)
            }
        }
        if (itemData.status == "Xong"){
            holder.iconStatus.setBackgroundResource(R.drawable.circular_green)
            holder.tvAction.visibility = View.GONE
        }


    }
}