package com.example.restaurant_management.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.Models.TableArea
import com.example.restaurant_management.R

interface IClickTableAreaItem{
    fun onUpdate(tableArea: TableArea, position: Int)
    fun onDelete(tableArea : TableArea, position: Int)
}

class TableAreaManagementAdapter(
    private var listTableArea: MutableList<TableArea>,
    private var listener : IClickTableAreaItem): RecyclerView.Adapter<TableAreaManagementViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TableAreaManagementViewHolder {
       var view = LayoutInflater.from(parent.context).inflate(R.layout.item_tableareamanagement, parent, false)
        return TableAreaManagementViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTableArea.size
    }

    override fun onBindViewHolder(holder: TableAreaManagementViewHolder, position: Int) {
        val itemData = listTableArea.get(position)
        holder.tableAreaName.setText(itemData.tableAreaName)
        holder.overFlowMenu.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.overFlowMenu)
            popupMenu.menu.add("Sửa thông tin")
            popupMenu.menu.add("Xóa")

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when(menuItem.title.toString()){
                    "Sửa thông tin" -> {
                        listener.onUpdate(itemData, position)
                        true
                    }
                    "Xóa" -> {
                        listener.onDelete(itemData, position)
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }
    }
}