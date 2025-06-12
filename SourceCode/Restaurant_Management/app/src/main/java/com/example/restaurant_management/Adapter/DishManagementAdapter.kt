package com.example.restaurant_management.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.DecimalFormat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Scroller
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.Models.Dish
import com.example.restaurant_management.R

interface IClickDishItem {
    fun onUpdate(dish: Dish, pos: Int)
    fun onDelete(dish: Dish, pos: Int)
    fun onChangeQuantity(dish: Dish, quantity: Int)
}

class DishManagementAdapter(
    var listDish: MutableList<Dish>,
    val listener: IClickDishItem,
    var screenName: String
) : RecyclerView.Adapter<DishManagementViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishManagementViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_dishmanagement, parent, false)
        return DishManagementViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listDish.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DishManagementViewHolder, position: Int) {
        val itemData = listDish.get(position)
        holder.tvDishName.setText(itemData.dishName)
        holder.tvDishCategoryName.setText(itemData.dishCategoryName)
        val formatprice = DecimalFormat("###,###.##").format(itemData.price)
        holder.tvPrice.setText(formatprice + " đ")
        holder.imvMenu.setOnClickListener {
            val popupmenu = PopupMenu(holder.itemView.context, holder.imvMenu)
            popupmenu.menu.add("Sửa thông tin")
            popupmenu.menu.add("Xóa")

            popupmenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title.toString()) {
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
            popupmenu.show()
        }
        var count = 0
        if (screenName == "Order" || screenName == "TableActive") {
            holder.imvMenu.visibility = View.GONE
            holder.itemView.setOnClickListener {
                holder.imvCong.visibility = View.VISIBLE
                holder.imvTru.visibility = View.VISIBLE
                holder.edtQuantity.visibility = View.VISIBLE
                count++
                holder.edtQuantity.setText(count.toString())
                listener.onChangeQuantity(itemData, count)
            }
            holder.imvTru.setOnClickListener {
                if (count > 0) {
                    count--
                    holder.edtQuantity.setText(count.toString())
                    listener.onChangeQuantity(itemData, count)
                }
            }

        }

    }

}