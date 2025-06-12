package com.example.restaurant_management.View

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant_management.Adapter.IClickOrderItem
import com.example.restaurant_management.Adapter.OrderItemAdapter
import com.example.restaurant_management.Controller.BillController
import com.example.restaurant_management.Controller.IBillView
import com.example.restaurant_management.Controller.IOrderView
import com.example.restaurant_management.Controller.ITableView
import com.example.restaurant_management.Controller.OrderController
import com.example.restaurant_management.Controller.TableController
import com.example.restaurant_management.Models.Bill
import com.example.restaurant_management.Models.Order
import com.example.restaurant_management.Models.OrderItem
import com.example.restaurant_management.Models.Table
import com.example.restaurant_management.Repository.Preferences
import com.example.restaurant_management.databinding.ActivityTableActiveBinding
import com.github.mikephil.charting.data.BarEntry

class TableActiveActivity : AppCompatActivity(), IOrderView, ITableView, IBillView {
    lateinit var bind: ActivityTableActiveBinding
    lateinit var adapter: OrderItemAdapter
    lateinit var listOrderItem: MutableList<OrderItem>
    lateinit var orderController: OrderController
    lateinit var tableController: TableController
    lateinit var billController: BillController
    lateinit var order: Order
    lateinit var tableId: String
    private lateinit var options: MutableList<Table>
    var table = Table()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityTableActiveBinding.inflate(layoutInflater)
        setContentView(bind.root)
        orderController = OrderController(this)
        tableController = TableController(this)
        billController = BillController(this)
        tableId = intent.getStringExtra("Id").toString()


        setSupportActionBar(bind.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val listener = object : IClickOrderItem {
            override fun onAddNote(orderItem: OrderItem, note: String) {
                val index = listOrderItem.indexOfFirst { it.dishId == orderItem.dishId }
                if (index != -1) {
                    listOrderItem[index].note = note
                }
                orderController.updateAllOrderItem(order.id!!, listOrderItem)
            }

            override fun onDelete(orderItem: OrderItem, pos: Int) {
                val index = listOrderItem.indexOfFirst { it.dishId == orderItem.dishId }
                if (index != -1) {
                    listOrderItem.removeAt(index)
                }
                orderController.updateAllOrderItem(order.id!!, listOrderItem)

            }

            override fun onChangeQuantity(orderItem: OrderItem, quantity: Int) {
                val index = listOrderItem.indexOfFirst { it.dishId == orderItem.dishId }
                if (index != -1) {
                    listOrderItem[index].quantity = quantity
                }
                orderController.updateAllOrderItem(order.id!!, listOrderItem)
            }

        }

        listOrderItem = mutableListOf()
        adapter = OrderItemAdapter(listOrderItem, listener)
        bind.rcListOrderItem.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.rcListOrderItem.adapter = adapter
        orderController.getByTableId(tableId!!)

        bind.btnAdd.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra("TableId", tableId)
            startActivity(intent)
        }

        bind.btnMenu.setOnClickListener {
            tableController.getById(tableId)
        }
        if (Preferences(this).getRole() == "Bếp" || Preferences(this).getRole() == "Phục vụ") {
            bind.btnBill.isEnabled = false
            bind.btnBill.alpha = 0.5f
        }
        bind.btnBill.setOnClickListener {
            val intent = Intent(this, BillActivity::class.java)
            intent.putExtra("orderId", order.id)
            startActivity(intent)
        }
        bind.btnThongBao.setOnClickListener {
            for (item in listOrderItem) {
                if (item.status == "") {
                    item.status = "Chưa làm"
                }
            }
            orderController.updateAllOrderItem(order.id!!, listOrderItem)
            finish()
        }
    }

    fun setBtnThongBao(list: MutableList<OrderItem>) {
        var check = false
        for (item in list) {
            if (item.status == "") {
                check = true
            }
        }
        if (check) {
            bind.btnThongBao.isEnabled = true
            bind.btnThongBao.alpha = 1f
        } else {
            bind.btnThongBao.isEnabled = false
            bind.btnThongBao.alpha = 0.5f

        }
    }

    fun showDialogChuyenBan() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("Chuyển bàn")
        val spinner = Spinner(this)
        tableController.getAllTableByStatusTrong()
        options = mutableListOf(Table("", "", "", "Tất cả", ""))


        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                table = p0?.getItemAtPosition(p2) as Table
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        builder.setView(spinner)
        builder.setPositiveButton("OK") { dialog, which ->
            if (table.tableName != "Tất cả") {
                orderController.changeTable(table, order)
            }
        }
        builder.setNegativeButton("Hủy") { dialog, which -> dialog.dismiss() }
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        orderController.getByTableId(tableId)
    }

    private fun showPopupMenu(table: Table) {
        val popupmenu = PopupMenu(this, bind.btnMenu)
        if (table.status == "Đang dùng") {
            popupmenu.menu.add("Bàn đặt")
        }
        popupmenu.menu.add("Chuyển bàn")
        popupmenu.menu.add("Lịch sử gọi món")
        popupmenu.menu.add("Hủy bàn")

        popupmenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title.toString()) {
                "Bàn đặt" -> {
                    AlertDialog.Builder(this)
                        .setTitle("Xác nhận chuyển thành bàn đặt")
                        .setMessage("Bàn này sẽ trở thành bàn đặt")
                        .setPositiveButton("Xác nhận") { dialog, which ->
                            tableController.changeStatus(
                                table.id!!,
                                "Đặt trước"
                            )
                        }
                        .setNegativeButton("Hủy") { dialog, which -> dialog.dismiss() }
                        .show()
                    true
                }

                "Chuyển bàn" -> {
                    showDialogChuyenBan()
                    true
                }

                "Lịch sử gọi món" -> {
                    var intent = Intent(this, HistoryOrderActivity::class.java)
                    intent.putExtra("orderId", order.id)
                    startActivity(intent)
                    true
                }

                "Hủy bàn" -> {
                    orderController.delete(order.id!!)
                    tableController.changeStatus(tableId, "Trống")
                    true
                }

                else -> false
            }
        }
        popupmenu.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOrderDataLoaded(data: Order?) {
        if (data != null) {
            order = data
            listOrderItem.clear()
            listOrderItem.addAll(data.orderItem)
            bind.toolbar.setTitle(data.tableName)
            bind.tvTotal.setText(getTotalString())
            setBtnThongBao(listOrderItem)
            for (item in listOrderItem) {
                if (item.orderId == "") {
                    item.orderId = order.id
                }
            }
            orderController.updateAllOrderItem(order.id!!, listOrderItem)
            adapter.notifyDataSetChanged()


        }
    }

    override fun onOrderDataListLoaded(dataList: List<Order>) {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOrderActionSuccess(message: String) {
        Log.d("5", message)
        if (message == "Chuyển bàn thành công") {
            tableController.changeStatus(tableId, "Trống")
            table.id?.let { tableController.changeStatus(it, "Đang dùng") }
            finish()
        }
        bind.tvTotal.setText(getTotalString())
        adapter.notifyDataSetChanged()
    }

    override fun onOrderActionError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
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

    fun getTotalString(): String {
        var sum = 0.0
        for (it in listOrderItem) {
            sum += it.price * it.quantity
        }
        val formatTotal = DecimalFormat("###,###.##").format(sum)
        return formatTotal
    }

    override fun onOrderItemDataListLoaded(dataList: List<OrderItem>) {

    }

    override fun onTableDataLoaded(data: Table?) {
        showPopupMenu(data!!)
    }

    override fun onTableDataListLoaded(dataList: List<Table>) {
        dataList.let {
            options.addAll(dataList)
        }
    }

    override fun onTableActionSuccess(message: String) {
        finish()
    }

    override fun onTableActionError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }


    // Bill
    override fun onBillDataLoaded(data: Bill?) {
    }

    override fun onBillListLoaded(dataList: List<Bill>) {
    }

    override fun onBarEntryListLoaded(dataList: List<BarEntry>) {

    }

    override fun onBillActionSuccess(message: String) {
        orderController.delete(order.id!!)
        tableController.changeStatus(order.tableId!!, "Trống")
        finish()
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
    }

    override fun onBillActionError(error: String) {
    }
}