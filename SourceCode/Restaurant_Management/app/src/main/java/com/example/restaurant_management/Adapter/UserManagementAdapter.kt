package com.example.restaurant_management.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.Models.User
import com.example.restaurant_management.R
import com.example.restaurant_management.Repository.UserRepository

//Create Interface
interface IClickUserItem {
    fun onUpdate(user: User, position: Int)
    fun onDelete(user: User, position: Int)
    fun onChangeIsActive(user: User, position: Int)
}

class UserManagementAdapter(private var listUser: MutableList<User>, val listener: IClickUserItem) :
    RecyclerView.Adapter<UserManagementViewHolder>() {
    var userRepository = UserRepository()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserManagementViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_usermanagement, parent, false)
        return UserManagementViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    override fun onBindViewHolder(holder: UserManagementViewHolder, position: Int) {
        val itemData = listUser.get(position)
        holder.fullName.setText(itemData.fullName)
        holder.role.setText(itemData.convertRoleToText(itemData.role!!))
        val color = if (itemData.active) R.drawable.active else R.drawable.inactive
        holder.isActive.setBackgroundResource(color)

        holder.imageButton.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.imageButton)
            popupMenu.menu.add("Sửa thông tin")
            popupMenu.menu.add("Đổi trang thái")
            popupMenu.menu.add("Xóa")

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title.toString()) {
                    "Sửa thông tin" -> {
                        listener.onUpdate(itemData, position)
                        true
                    }

                    "Đổi trạng thái" -> {
                        listener.onChangeIsActive(itemData, position)
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