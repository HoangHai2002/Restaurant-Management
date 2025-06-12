package com.example.restaurant_management.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.Models.DishCategory
import com.example.restaurant_management.Models.TableArea
import com.example.restaurant_management.R

interface IClickDishCategoryMainItem{
    fun onClick(data: TableArea, pos: Int, selectedPos: Int)
}
class TableAreaMainAdapter(var lishDishCategory : MutableList<TableArea>, var listener : IClickDishCategoryMainItem): RecyclerView.Adapter<TableAreaMainViewHolder>() {
    var selectedPosition = RecyclerView.NO_POSITION
    @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableAreaMainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tablearea_main, parent, false)
        return TableAreaMainViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lishDishCategory.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TableAreaMainViewHolder, position: Int) {
        val itemData = lishDishCategory.get(position)
        holder.tvTableAreaName.setText(itemData.tableAreaName)
        if (position == selectedPosition) {
            holder.tvTableAreaName.setBackgroundResource(R.drawable.background_khuvucban)
            holder.tvTableAreaName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        } else {
            holder.tvTableAreaName.setBackgroundResource(R.color.white)
            holder.tvTableAreaName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
        }

        holder.itemView.setOnClickListener {
            // Lưu vị trí item được chọn
            if (selectedPosition == RecyclerView.NO_POSITION) {
                selectedPosition = position
            } else {
                if (selectedPosition == position) {
                    selectedPosition = RecyclerView.NO_POSITION // Xóa vị trí đã chọn
                } else {
                    selectedPosition = position
                }
            }


            // Thông báo cho Adapter cần phải cập nhật lại toàn bộ danh sách
            notifyDataSetChanged()

            listener.onClick(itemData, position, selectedPosition)

        }

    }
}