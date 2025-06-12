package com.example.restaurant_management.View

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurant_management.Controller.BillController
import com.example.restaurant_management.Controller.IBillView
import com.example.restaurant_management.Controller.IOrderView
import com.example.restaurant_management.Controller.ITableView
import com.example.restaurant_management.Controller.IUserView
import com.example.restaurant_management.Controller.OrderController
import com.example.restaurant_management.Controller.TableController
import com.example.restaurant_management.Controller.UserController
import com.example.restaurant_management.Models.Bill
import com.example.restaurant_management.Models.Order
import com.example.restaurant_management.Models.OrderItem
import com.example.restaurant_management.Models.Table
import com.example.restaurant_management.Models.User
import com.example.restaurant_management.Repository.Preferences
import com.example.restaurant_management.databinding.ActivityBillBinding
import com.github.mikephil.charting.data.BarEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BillActivity : AppCompatActivity(), IOrderView, IUserView, IBillView,ITableView {
    lateinit var orderController: OrderController
    lateinit var userController: UserController
    lateinit var bind: ActivityBillBinding
    lateinit var orderMergeOrderItem: Order
    var sum = 0.0
    var tiengiam = 0.0
    var tongthu = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityBillBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setSupportActionBar(bind.toolbar4)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.toolbar4.setTitle("Hóa đơn thanh toán")

        val orderId = intent.getStringExtra("orderId")
        orderController = OrderController(this)
        orderId?.let {
            orderController.getById(it)
        }


        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val dateString = sdf.format(Date())
        bind.tvCreatedAt.setText(dateString)

        userController = UserController((this))
        val userId = Preferences(this).getId()
        userId?.let {
            userController.getById(it)
        }

        var selectedSell: RadioButton? = bind.rbtn1
        bind.rgSell.setOnCheckedChangeListener { group, checkedId ->
            bind.edtGiamgia.setText("")
            val formatTotal = DecimalFormat("###,###.##").format(sum)
            bind.tvTongThu.setText(formatTotal)

            selectedSell = if (checkedId != -1) {
                findViewById<RadioButton>(checkedId)
            } else {
                null
            }

            when (selectedSell) {
                bind.rbtn1 -> bind.tvDiscountType.setText("%")
                bind.rbtn2 -> bind.tvDiscountType.setText("VNĐ")
                else -> bind.edtGiamgia.hint = ""
            }
        }

        bind.edtGiamgia.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val input = p0.toString().toDoubleOrNull()
                if (input == null) {
                    bind.tvTienGiam.setText("0")
                    bind.tvTongThu.setText(sum.toString())
                    return
                }

                if (selectedSell == bind.rbtn1) {
                    if (input <= 100) {
                        tiengiam = sum  * input / 100
                        val formatTienGiam = DecimalFormat("###,###.##").format(tiengiam)
                        bind.tvTienGiam.setText(formatTienGiam)


                    }else{
                        bind.edtGiamgia.setText("100")
                        tiengiam = sum  * 100 / 100
                        val formatTienGiam = DecimalFormat("###,###.##").format(tiengiam)
                        bind.tvTienGiam.setText(formatTienGiam)
                    }
                    tongthu = sum-tiengiam
                    val formatTongThu = DecimalFormat("###,###.##").format(tongthu)
                    bind.tvTongThu.setText(formatTongThu)

                }
                if(selectedSell == bind.rbtn2){
                    if (input > sum){
                        val formatedtTienGiam = DecimalFormat("###,###.##").format(sum)
                        bind.edtGiamgia.setText(formatedtTienGiam)
                        tiengiam = sum
                        val formatTienGiam = DecimalFormat("###,###.##").format(tiengiam)
                        bind.tvTienGiam.setText(formatTienGiam)
                        tongthu = sum-tiengiam
                        val formatTongThu = DecimalFormat("###,###.##").format(tongthu)
                        bind.tvTongThu.setText(formatTongThu)
                    }else{
                        tiengiam = input
                        val formatTienGiam = DecimalFormat("###,###.##").format(tiengiam)
                        bind.tvTienGiam.setText(formatTienGiam)
                        tongthu = sum-tiengiam
                        val formatTongThu = DecimalFormat("###,###.##").format(tongthu)
                        bind.tvTongThu.setText(formatTongThu)
                    }

                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        var selectedPaymentMethod: RadioButton? = bind.rbtnCK
        bind.rgPaymentMethod.setOnCheckedChangeListener { radioGroup, i ->
            selectedPaymentMethod = if (i != -1){
                findViewById<RadioButton>(i)
            }else{
                null
            }
        }
        bind.btnHuy.setOnClickListener {
            finish()
        }
        bind.btnThanhtoan.setOnClickListener {
            var discount = bind.edtGiamgia.text.toString().toIntOrNull()
            if (discount == null){
                discount = 0
            }
            var total = tongthu

            val discountType = selectedSell?.text.toString()
            val paymentMethod = selectedPaymentMethod?.text.toString()
            val createdAt = bind.tvCreatedAt.text.toString()
            val createdBy = bind.tvCreatedBy.text.toString()
            val newBill = Bill("",discount, total,  discountType, paymentMethod, createdAt, createdBy, orderMergeOrderItem)
            Log.d("Bill", newBill.toString())
            BillController(this).add(newBill)
        }

    }

    fun mergeOrderItems(items: List<OrderItem>): List<OrderItem> {
        return items
            .groupBy { it.dishId }
            .map { entry ->
                val merged = entry.value.first().copy()
                merged.quantity = entry.value.sumOf { it.quantity }
                merged
            }
    }

    override fun onOrderDataLoaded(data: Order?) {
        data?.let {
            orderMergeOrderItem = data
            orderMergeOrderItem.orderItem = mergeOrderItems(it.orderItem).toMutableList()
            var count = 0
            for (item in mergeOrderItems(it.orderItem)) {
                count++
                val row = TableRow(this)
                val cell1 = TextView(this)
                cell1.setText(count.toString())
                cell1.setPadding(0, 10, 0, 10)
                cell1.gravity = Gravity.CENTER

                val cell2 = TextView(this)
                cell2.setText(item.dishName)
                cell2.textSize = 16f
                cell2.gravity = Gravity.CENTER

                val cell3 = TextView(this)
                val formatPrice = DecimalFormat("###,###.##").format(item.price)
                cell3.setText(formatPrice)
                cell3.textSize = 16f
                cell3.gravity = Gravity.CENTER

                val cell4 = TextView(this)
                cell4.setText(item.quantity.toString())
                cell4.gravity = Gravity.CENTER

                row.addView(cell1)
                row.addView(cell2)
                row.addView(cell4)
                row.addView(cell3)
                bind.tableLayout.addView(row)

                sum += item.price * item.quantity
            }
            val formatTotal = DecimalFormat("###,###.##").format(sum)
            bind.tvTotal.setText(formatTotal)
            tongthu = sum
            bind.tvTongThu.setText(formatTotal)
        }
    }

    override fun onOrderDataListLoaded(dataList: List<Order>) {
    }

    override fun onOrderItemDataListLoaded(dataList: List<OrderItem>) {
    }

    override fun onOrderActionSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

    override fun onUserDataLoaded(data: User?) {
        data?.let {
            bind.tvCreatedBy.setText(data.fullName)
        }
    }

    override fun onUserDataListLoaded(dataList: List<User>) {
    }

    override fun onUserActionSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onUserActionError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onBillDataLoaded(data: Bill?) {
    }

    override fun onBillListLoaded(dataList: List<Bill>) {
    }

    override fun onBarEntryListLoaded(dataList: List<BarEntry>) {
    }

    override fun onBillActionSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        if (message == "Thanh toán thành công"){
            orderController.delete(orderMergeOrderItem.id!!)
            TableController(this).changeStatus(orderMergeOrderItem.tableId!!, "Trống")

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
    }

    override fun onBillActionError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onTableDataLoaded(data: Table?) {
    }

    override fun onTableDataListLoaded(dataList: List<Table>) {
    }

    override fun onTableActionSuccess(message: String) {
    }

    override fun onTableActionError(error: String) {
    }
}