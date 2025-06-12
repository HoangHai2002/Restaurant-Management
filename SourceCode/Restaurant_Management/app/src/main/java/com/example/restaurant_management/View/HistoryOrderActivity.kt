package com.example.restaurant_management.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant_management.Adapter.HistoryOrderAdapter
import com.example.restaurant_management.Controller.IOrderView
import com.example.restaurant_management.Controller.OrderController
import com.example.restaurant_management.Models.Order
import com.example.restaurant_management.Models.OrderItem
import com.example.restaurant_management.R
import com.example.restaurant_management.databinding.ActivityHistoryOrderBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryOrderActivity : AppCompatActivity(), IOrderView {
    lateinit var bind : ActivityHistoryOrderBinding
    lateinit var listOrderItem : MutableList<OrderItem>
    lateinit var adapter : HistoryOrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityHistoryOrderBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setSupportActionBar(bind.toolbar4)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        listOrderItem = mutableListOf()
        adapter = HistoryOrderAdapter(listOrderItem)
        bind.rcHistoryOrder.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.rcHistoryOrder.adapter = adapter

        var orderId = intent.getStringExtra("orderId")
        if (orderId != null) {
            OrderController(this).getById(orderId)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                finish()
                true
            }else -> false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOrderDataLoaded(data: Order?) {
        data?.let {

            bind.toolbar4.setTitle("Lich sử gọi món "+data.tableName)
            listOrderItem.clear()
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

            val sortedList = data.orderItem.sortedByDescending  {
                formatter.parse(it.orderAt)
            }
            listOrderItem.addAll(sortedList)

            adapter.notifyDataSetChanged()
        }
    }

    override fun onOrderDataListLoaded(dataList: List<Order>) {
    }

    override fun onOrderItemDataListLoaded(dataList: List<OrderItem>) {
    }

    override fun onOrderActionSuccess(message: String) {
    }

    override fun onOrderActionError(error: String) {
    }
}