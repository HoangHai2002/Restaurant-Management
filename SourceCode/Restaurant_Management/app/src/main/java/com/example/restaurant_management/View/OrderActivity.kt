package com.example.restaurant_management.View

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant_management.Adapter.DishManagementAdapter
import com.example.restaurant_management.Adapter.IClickDishItem
import com.example.restaurant_management.Controller.DishController
import com.example.restaurant_management.Controller.IDishView
import com.example.restaurant_management.Controller.IOrderView
import com.example.restaurant_management.Controller.ITableView
import com.example.restaurant_management.Controller.OrderController
import com.example.restaurant_management.Controller.TableController
import com.example.restaurant_management.Models.Dish
import com.example.restaurant_management.Models.Order
import com.example.restaurant_management.Models.OrderItem
import com.example.restaurant_management.Models.Table
import com.example.restaurant_management.Repository.Preferences
import com.example.restaurant_management.databinding.ActivityOrderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderActivity : AppCompatActivity(), IDishView, IOrderView, ITableView {
    lateinit var bind: ActivityOrderBinding
    lateinit var adapter: DishManagementAdapter
    lateinit var listDish: MutableList<Dish>
    lateinit var dishController: DishController
    lateinit var orderController: OrderController
    lateinit var listOrderItem: MutableList<OrderItem>
    lateinit var table: Table
    lateinit var tableId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(bind.root)
        tableId = intent.getStringExtra("TableId").toString()
        dishController = DishController(this)
        orderController = OrderController(this)
        listOrderItem = mutableListOf()
        TableController(this).getById(tableId)


        setSupportActionBar(bind.toolbar6)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.toolbar6.setTitle("Gọi món")


        val listener = object : IClickDishItem {
            override fun onUpdate(dish: Dish, pos: Int) {

            }

            override fun onDelete(dish: Dish, pos: Int) {

            }

            override fun onChangeQuantity(dish: Dish, quantity: Int) {
                if (quantity > 0) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    val dateString: String = sdf.format(Date())
                    val index = listOrderItem.indexOfFirst { it.dishId == dish.id }
                    if (index != -1) {
                        listOrderItem[index].quantity = quantity
                    } else {
                        listOrderItem.add(
                            OrderItem(
                                "",
                                "",
                                dish.id,
                                dish.dishName,
                                table.tableName,
                                quantity,
                                dish.price,
                                "",
                                Preferences(this@OrderActivity).getId(),
                                dateString,
                                ""
                            )
                        )
                    }
                } else{
                    val index = listOrderItem.indexOfFirst { it.dishId == dish.id }
                    if (index != -1) {
                        listOrderItem.removeAt(index)
                    }
                }
            }
        }
        listDish = mutableListOf()
        adapter = DishManagementAdapter(listDish, listener, "Order")
        bind.rcListDish.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.rcListDish.adapter = adapter
        dishController.getAll()


        bind.btnHuy.setOnClickListener {
            finish()
        }
        bind.btnThem.setOnClickListener {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val dateString: String = sdf.format(Date())

            // Add for status == Trong
            if (table.status == "Trống") {
                var total = 0.0
                for (orderItem in listOrderItem) {
                    total += orderItem.price * orderItem.quantity
                }
                val newOrder = Order(
                    "",
                    table.id,
                    table.tableName,
                    total,
                    true,
                    "",
                    0,
                    dateString,
                    listOrderItem
                )
                orderController.add(newOrder)

                //Update but status != Trong
            } else {
                orderController.getByTableId(tableId)

            }


        }

        // Search
        bind.edtSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun afterTextChanged(p0: Editable?) {
                val keyword = p0.toString().lowercase().trim()
                if (keyword == ""){
                    dishController.getAll()
                }else{
                    val sortedList = listDish.sortedWith(compareByDescending<Dish>{
                        it.dishName?.lowercase()?.startsWith(keyword) == true
                    }.thenByDescending {
                        it.dishName?.lowercase()?.contains(keyword) == true
                    })
                    listDish.clear()
                    listDish.addAll(sortedList)
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> false
        }
    }

    override fun onDishDataLoaded(data: Dish?) {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDishDataListLoaded(dataList: List<Dish>) {
        listDish.clear()
        listDish.addAll(dataList)
        adapter.notifyDataSetChanged()
    }

    override fun onDishActionSuccess(message: String) {
    }

    override fun onDishActionError(error: String) {
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && view is EditText && ev.action == MotionEvent.ACTION_DOWN) {
            val outRect = Rect()
            view.getGlobalVisibleRect(outRect)
            if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                view.clearFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onOrderDataLoaded(data: Order?) {
        listOrderItem.addAll(data?.orderItem!!)
        orderController.updateAllOrderItem(data.id!!, listOrderItem)


    }

    override fun onOrderDataListLoaded(dataList: List<Order>) {
    }

    override fun onOrderActionSuccess(message: String) {
        TableController(this).changeStatus(table.id!!, "Đang dùng")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        if (message == "Success"){
            finish()
        }else{
            val intent = Intent(this, TableActiveActivity::class.java)
            intent.putExtra("Id", tableId)
            startActivity(intent)
            finish()
        }

    }

    override fun onOrderItemDataListLoaded(dataList: List<OrderItem>) {

    }

    override fun onOrderActionError(error: String) {
    }

    override fun onTableDataLoaded(data: Table?) {
        table = data!!
    }

    override fun onTableDataListLoaded(dataList: List<Table>) {
    }

    override fun onTableActionSuccess(message: String) {
    }

    override fun onTableActionError(error: String) {
    }
}