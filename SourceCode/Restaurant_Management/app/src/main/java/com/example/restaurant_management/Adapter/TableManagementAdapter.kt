package com.example.restaurant_management.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.Models.Table
import com.example.restaurant_management.R
import kotlin.properties.Delegates

interface IClickTableItem{
    fun onUpdate(table: Table, position: Int)
    fun onDelete(table: Table, position: Int)
}

class TableManagementAdapter(var listTable : MutableList<Table>, val listener : IClickTableItem) : RecyclerView.Adapter<TableManagementHolder>(){
    private var color by Delegates.notNull<Int>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableManagementHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tablemanagement, parent, false)
        return TableManagementHolder(view)
    }

    override fun getItemCount(): Int {
       return listTable.size
    }

    override fun onBindViewHolder(holder: TableManagementHolder, position: Int) {
        val itemData = listTable.get(position)

        when(itemData.status){
            "Đang dùng" -> color = R.drawable.circular_blue
            "Trống" -> color = R.drawable.circular_green
            "Đặt trước" -> color = R.drawable.circular_orange
        }
        holder.iconStatus.setBackgroundResource(color)
        holder.tvStatus.setText("[          ${itemData.status} ]")
        holder.tvTableName.setText(itemData.tableName)
        holder.tvTableArea.setText(itemData.tableAreaName)

        holder.menu.setOnClickListener {
            val popupmenu = PopupMenu(holder.itemView.context, holder.menu)
            popupmenu.menu.add("Sửa thông tin")
            popupmenu.menu.add("Xóa")

            popupmenu.setOnMenuItemClickListener {  menuItem ->
                when(menuItem.title.toString()){
                    "Sửa thông tin" ->{
                        listener.onUpdate(itemData, position)
                        true
                    }
                    "Xóa" ->{
                        listener.onDelete(itemData, position)
                        true
                    }
                    else -> false
                }
            }
            popupmenu.show()
        }
    }
}