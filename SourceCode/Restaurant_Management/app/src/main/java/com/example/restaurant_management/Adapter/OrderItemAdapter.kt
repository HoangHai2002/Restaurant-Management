package com.example.restaurant_management.Adapter

import android.content.Context
import android.icu.text.DecimalFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.Models.Dish
import com.example.restaurant_management.Models.Order
import com.example.restaurant_management.Models.OrderItem
import com.example.restaurant_management.Models.TableArea
import com.example.restaurant_management.R

interface IClickOrderItem{
    fun onAddNote(orderItem : OrderItem, note : String)
    fun onDelete(orderItem : OrderItem, pos : Int)
    fun onChangeQuantity(orderItem : OrderItem, quantity : Int)
}
class OrderItemAdapter(var listOrderItem : MutableList<OrderItem>, var listener : IClickOrderItem): RecyclerView.Adapter<DishManagementViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishManagementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dishmanagement, parent, false)
        return DishManagementViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOrderItem.size
    }

    override fun onBindViewHolder(holder: DishManagementViewHolder, position: Int) {
        val itemData = listOrderItem.get(position)
        holder.tvDishName.setText(itemData.dishName)
        holder.tvDishCategoryName.setText(itemData.note)
        holder.edtQuantity.setText(itemData.quantity.toString())
        val formatprice = DecimalFormat("###,###.##").format( itemData.price)
        holder.tvPrice.setText(formatprice)
        if (itemData.status == ""){
            holder.imvCong.visibility = View.VISIBLE
            holder.imvTru.visibility = View.VISIBLE
            holder.edtQuantity.visibility = View.VISIBLE


            var count = itemData.quantity

            // Change quantity
            holder.itemView.setOnClickListener{
                count++
                holder.edtQuantity.setText(count.toString())
                listener.onChangeQuantity(itemData, count)
            }

            holder.imvTru.setOnClickListener {
                if(count > 1){
                    count--
                    holder.edtQuantity.setText(count.toString())
                    listener.onChangeQuantity(itemData, count)
                }
            }

            holder.imvMenu.setOnClickListener {
                val popupmenu = PopupMenu(holder.itemView.context, holder.imvMenu)
                popupmenu.menu.add("Thêm ghi chú")
                popupmenu.menu.add("Hủy món")

                popupmenu.setOnMenuItemClickListener {  menuItem ->
                    when(menuItem.title.toString()){
                        "Thêm ghi chú" ->{
                            val builder = AlertDialog.Builder(holder.itemView.context)
                            builder.setTitle("Thêm ghi chú")
                            val note = EditText(holder.itemView.context)
                            note.hint = "Nhập ghi chú"
                            val layout = LinearLayout(holder.itemView.context)
                            layout.orientation  = LinearLayout.VERTICAL
                            layout.setPadding(50,40,50,10)
                            layout.addView(note)
                            builder.setView(layout)
                            builder.setPositiveButton("Thêm"){dialog, which ->
                                holder.tvDishCategoryName.setText(note.text.toString().trim())
                                listener.onAddNote(itemData, note.text.toString().trim())
                            }
                            builder.setNegativeButton("Hủy"){dialog, which->
                                dialog.dismiss()
                            }
                            builder.show()
                            true
                        }
                        "Hủy món" ->{
                            listener.onDelete(itemData, position)
                            true
                        }
                        else -> false
                    }
                }
                popupmenu.show()
            }
        }else{
            holder.edtQuantity.visibility = View.VISIBLE
            holder.imvMenu.setOnClickListener {
                val popupmenu = PopupMenu(holder.itemView.context, holder.imvMenu)
                popupmenu.menu.add("Hủy món")

                popupmenu.setOnMenuItemClickListener {  menuItem ->
                    when(menuItem.title.toString()){
                        "Hủy món" ->{
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

}