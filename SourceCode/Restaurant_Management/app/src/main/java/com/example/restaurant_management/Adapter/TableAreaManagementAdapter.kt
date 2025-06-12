package com.example.restaurant_management.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.Controller.IOrderView
import com.example.restaurant_management.Controller.ITableView
import com.example.restaurant_management.Controller.OrderController
import com.example.restaurant_management.Controller.TableController
import com.example.restaurant_management.Models.Order
import com.example.restaurant_management.Models.OrderItem
import com.example.restaurant_management.Models.Table
import com.example.restaurant_management.Models.TableArea
import com.example.restaurant_management.R

interface IClickTableAreaItem{
    fun onUpdate(tableArea: TableArea, position: Int)
    fun onDelete(tableArea : TableArea, position: Int)
}

class TableAreaManagementAdapter(
    private var listTableArea: MutableList<TableArea>,
    private var listener : IClickTableAreaItem): RecyclerView.Adapter<TableAreaManagementViewHolder>() {
    lateinit var controller: TableController
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

        controller = TableController(object : ITableView {
            override fun onTableDataLoaded(data: Table?) {
            }

            override fun onTableDataListLoaded(dataList: List<Table>) {
                dataList?.let {
                    var count = 0
                    for (table in it){
                        if (itemData.id == table.tableAreaId)   count++
                    }
                    holder.slBan.setText(count.toString()+" Bàn")
                }
            }

            override fun onTableActionSuccess(message: String) {
            }

            override fun onTableActionError(error: String) {
            }
        })
        controller.getAll()

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