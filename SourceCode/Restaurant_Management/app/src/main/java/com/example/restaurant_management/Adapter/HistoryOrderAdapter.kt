package com.example.restaurant_management.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.Controller.IOrderView
import com.example.restaurant_management.Controller.IUserView
import com.example.restaurant_management.Controller.OrderController
import com.example.restaurant_management.Controller.UserController
import com.example.restaurant_management.Models.Order
import com.example.restaurant_management.Models.OrderItem
import com.example.restaurant_management.Models.User
import com.example.restaurant_management.R

class HistoryOrderAdapter(var listOrderItem : MutableList<OrderItem>):RecyclerView.Adapter<HistoryViewHolder>() {
    lateinit var controller: UserController
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historyorder, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOrderItem.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val itemData = listOrderItem.get(position)
        holder.tvDishName.setText(itemData.dishName)
        holder.tvQuantity.setText(itemData.quantity.toString())
        holder.tvCreatedAt.setText(itemData.orderAt)
        controller = UserController(object : IUserView {
            override fun onUserDataLoaded(data: User?) {
                data?.let{
                    holder.tvCreatedBy.setText(data.fullName)
                }
            }
            override fun onUserDataListLoaded(dataList: List<User>) {
            }

            override fun onUserActionSuccess(message: String) {
            }

            override fun onUserActionError(error: String) {
            }
        })
        itemData.orderBy?.let { controller.getById(it) }
    }
}