package com.example.restaurant_management.Adapter

import android.icu.text.DecimalFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.Models.Bill
import com.example.restaurant_management.R

interface IClickBillItem{
    fun onClick(data : Bill)
}

class BillAdapter(var listBill : MutableList<Bill>, var listener : IClickBillItem):RecyclerView.Adapter<BillViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bill, parent, false)
        return BillViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listBill.size
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        var itemData = listBill.get(position)
        holder.tvCreatedAt.setText(itemData.createdAt)
        holder.tvTableName.setText(itemData.order?.tableName)
        val formatTotal = DecimalFormat("##,###.###").format(itemData.total)
        holder.tvTotal.setText(formatTotal)
        holder.tvPaymentType.setText(itemData.paymentMethod)
        holder.itemView.setOnClickListener {
            listener.onClick(itemData)
        }
    }
}