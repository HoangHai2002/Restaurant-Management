package com.example.restaurant_management.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant_management.Adapter.IClickDishKitchen
import com.example.restaurant_management.Adapter.KitchenAdapter
import com.example.restaurant_management.Controller.IOrderView
import com.example.restaurant_management.Controller.OrderController
import com.example.restaurant_management.Models.Order
import com.example.restaurant_management.Models.OrderItem
import com.example.restaurant_management.R
import com.example.restaurant_management.databinding.ActivityKitchenBinding

class KitchenActivity : AppCompatActivity(), IOrderView {
    lateinit var bind : ActivityKitchenBinding
    lateinit var listOrderItem: MutableList<OrderItem>
    lateinit var adapter : KitchenAdapter
    lateinit var orderController: OrderController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityKitchenBinding.inflate(layoutInflater)
        setContentView(bind.root)
        orderController = OrderController(this)

        setSupportActionBar(bind.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.toolbar.setTitle("Quản lý đơn bếp")

        val listener = object : IClickDishKitchen {
            @SuppressLint("NotifyDataSetChanged")
            override fun onClickStart(orderItem: OrderItem, position: Int) {
                orderController.updateStatusOrderItem(orderItem, "Đang làm")
            }

            override fun onClickSuccess(orderItem: OrderItem, position: Int) {
                orderController.updateStatusOrderItem(orderItem, "Xong")
            }
        }
        val option = mutableListOf("Tất cả", "Lọc theo thời gian")

        listOrderItem = mutableListOf()
        adapter = KitchenAdapter(listOrderItem, listener)
        bind.rcListDishKitchen.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.rcListDishKitchen.adapter = adapter
        orderController.getOrderItemByStatus("Chưa làm")

        var select = 1
       when(select){
           1 ->{
               bind.btnChuaLam.setBackgroundResource(R.drawable.select)
               bind.btnDangLam.background = null
               bind.btnXong.background = null
           }
           2 ->{
               bind.btnDangLam.setBackgroundResource(R.drawable.select)
               bind.btnChuaLam.background = null
               bind.btnXong.background = null
           }
           3 ->{
               bind.btnXong.setBackgroundResource(R.drawable.select)
               bind.btnDangLam.background = null
               bind.btnChuaLam.background = null
           }
       }

        bind.btnChuaLam.setOnClickListener {
            orderController.getOrderItemByStatus("Chưa làm")
            bind.btnChuaLam.setBackgroundResource(R.drawable.select)
            bind.btnDangLam.background = null
            bind.btnXong.background = null
        }
        bind.btnDangLam.setOnClickListener {
            orderController.getOrderItemByStatus("Đang làm")
            bind.btnDangLam.setBackgroundResource(R.drawable.select)
            bind.btnChuaLam.background = null
            bind.btnXong.background = null

        }
        bind.btnXong.setOnClickListener {
            orderController.getOrderItemByStatus("Xong")

            bind.btnXong.setBackgroundResource(R.drawable.select)
            bind.btnDangLam.background = null
            bind.btnChuaLam.background = null
        }

    }

    override fun onOrderDataLoaded(data: Order?) {
    }

    override fun onOrderDataListLoaded(dataList: List<Order>) {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOrderItemDataListLoaded(dataList: List<OrderItem>) {
        listOrderItem.clear()
        listOrderItem.addAll(dataList)
        adapter.notifyDataSetChanged()
    }

    override fun onOrderActionSuccess(message: String) {
        if (message == "Đang làm"){
            orderController.getOrderItemByStatus("Chưa làm")
        }
        if (message == "Xong"){
            orderController.getOrderItemByStatus("Đang làm")
        }
    }

    override fun onOrderActionError(error: String) {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                finish()
                true
            }
            else -> false
        }
    }
}