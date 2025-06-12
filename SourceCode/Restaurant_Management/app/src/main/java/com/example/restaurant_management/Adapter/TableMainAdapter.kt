package com.example.restaurant_management.Adapter

import android.annotation.SuppressLint
import android.icu.text.DecimalFormat
import android.icu.text.Transliterator.Position
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.Controller.IOrderView
import com.example.restaurant_management.Controller.OrderController
import com.example.restaurant_management.Models.Order
import com.example.restaurant_management.Models.OrderItem
import com.example.restaurant_management.Models.Table
import com.example.restaurant_management.R


interface IClickTableMainItem{
    fun onClick(data : Table)
}
class TableMainAdapter(var lishTable : MutableList<Table>, var listener : IClickTableMainItem): RecyclerView.Adapter<TableMainViewHolder>() {
    lateinit var controller: OrderController

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableMainViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_table_main, parent, false)
        return TableMainViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lishTable.size
    }

    override fun onBindViewHolder(holder: TableMainViewHolder, position: Int) {
        val itemData = lishTable.get(position)
        holder.tvTableName.setText(itemData.tableName)

        holder.tvGuestCount.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
                .setTitle("Số lượng khách")
            val input = EditText(holder.itemView.context)
            input.hint = "Nhập số lượng khách"
            input.inputType = InputType.TYPE_CLASS_NUMBER
            builder.setView(input)
            builder.setPositiveButton("OK"){dialog, which ->
                holder.tvGuestCount.setText(input.text.toString()+" Khách")
                controller = OrderController(object : IOrderView {
                    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
                    override fun onOrderDataLoaded(data: Order?) {
                        controller.updateGuest(data?.id!!, input.text.toString().toInt())
                    }

                    override fun onOrderItemDataListLoaded(dataList: List<OrderItem>) {}
                    override fun onOrderDataListLoaded(dataList: List<Order>) {}
                    override fun onOrderActionSuccess(message: String) {

                    }
                    override fun onOrderActionError(error: String) {}
                })
                controller.getByTableId(itemData.id!!)
            }
            builder.setNegativeButton("Hủy"){dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }
        if (itemData.status == "Đang dùng") {
            holder.tvTotal.visibility = View.VISIBLE
            holder.tvGuestCount.visibility = View.VISIBLE
            holder.tvNote.visibility = View.VISIBLE
            holder.layout.setBackgroundResource(R.drawable.background_table_selected)

            // Total
            controller = OrderController(object : IOrderView {
                @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
                override fun onOrderDataLoaded(data: Order?) {
                    data?.let { order ->
                        Log.d("Order", order.toString())
                        val formatTotal = DecimalFormat("###,###.##").format(order.total)
                        holder.tvTotal.setText(formatTotal+"đ")
                        holder.tvGuestCount.setText(data.guest.toString()+" Khách")
                    }
                }

                override fun onOrderDataListLoaded(dataList: List<Order>) {}
                override fun onOrderItemDataListLoaded(dataList: List<OrderItem>) {
                }

                override fun onOrderActionSuccess(message: String) {}
                override fun onOrderActionError(error: String) {}
            })
            controller.getByTableId(itemData.id!!)

        }
        if (itemData.status == "Đặt trước") {
            holder.tvTotal.visibility = View.VISIBLE
            holder.tvGuestCount.visibility = View.VISIBLE
            holder.tvNote.visibility = View.VISIBLE
            holder.layout.setBackgroundResource(R.drawable.background_table_reservation)

            // Total
            controller = OrderController(object : IOrderView {
                @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
                override fun onOrderDataLoaded(data: Order?) {
                    data?.let { order ->
                        Log.d("Order", order.toString())
                        val formatTotal = DecimalFormat("###,###.##").format(order.total)
                        holder.tvTotal.setText(formatTotal+"đ")
                        holder.tvGuestCount.setText(data.guest.toString()+" Khách")
                    }
                }

                override fun onOrderItemDataListLoaded(dataList: List<OrderItem>) {

                }

                override fun onOrderDataListLoaded(dataList: List<Order>) {}
                override fun onOrderActionSuccess(message: String) {}
                override fun onOrderActionError(error: String) {}
            })
            controller.getByTableId(itemData.id!!)
        }
        if (itemData.status == "Trống") {
            holder.tvTotal.visibility = View.GONE
            holder.tvGuestCount.visibility = View.GONE
            holder.tvNote.visibility = View.GONE
            holder.layout.setBackgroundResource(R.drawable.background_table_unselected)
        }
        holder.itemView.setOnClickListener {
            listener.onClick(itemData)
        }
    }


}