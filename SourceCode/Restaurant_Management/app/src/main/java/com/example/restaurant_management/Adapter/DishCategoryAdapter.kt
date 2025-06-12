package com.example.restaurant_management.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.Models.Dish
import com.example.restaurant_management.Models.DishCategory
import com.example.restaurant_management.R

interface IClickDishCategoryItem{
    fun onUpdate(dishCategory : DishCategory, pos : Int)
    fun onDelete(dishCategory : DishCategory, pos : Int)
}
class DishCategoryAdapter(var lishDishCategory: MutableList<DishCategory>, var listener : IClickDishCategoryItem): RecyclerView.Adapter<DishCategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishCategoryViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dishcategorymanagement, parent, false)
        return DishCategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lishDishCategory.size
    }

    override fun onBindViewHolder(holder: DishCategoryViewHolder, position: Int) {
        val itemData = lishDishCategory.get(position)
        holder.tvDishCategoryName.setText(itemData.dishCategoryName)
        holder.imvMenu.setOnClickListener {
            val popupmenu = PopupMenu(holder.itemView.context, holder.imvMenu)
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
