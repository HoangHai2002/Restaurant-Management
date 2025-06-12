package com.example.restaurant_management.View

import android.icu.text.DecimalFormat
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.restaurant_management.Controller.BillController
import com.example.restaurant_management.Controller.IBillView
import com.example.restaurant_management.Controller.IOrderView
import com.example.restaurant_management.Controller.OrderController
import com.example.restaurant_management.Models.Bill
import com.example.restaurant_management.Models.Order
import com.example.restaurant_management.Models.OrderItem
import com.example.restaurant_management.R
import com.example.restaurant_management.databinding.ActivityBillDetailBinding
import com.github.mikephil.charting.data.BarEntry

class BillDetailActivity : AppCompatActivity(), IBillView{
    lateinit var bind : ActivityBillDetailBinding
    lateinit var controller: BillController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityBillDetailBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setSupportActionBar(bind.toolbar4)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.toolbar4.setTitle("Chi tiết hóa đơn")

        val billId = intent.getStringExtra("billId")
        controller = BillController(this)
        billId?.let {
            controller.getById(billId)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                finish()
                true
            }else -> false
        }
    }

    override fun onBillDataLoaded(data: Bill?) {
        data?.let {
            var count = 0
            var list = it.order?.orderItem
            if (list != null) {
                for (item in list) {
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
                }
            }
            bind.tvCreatedAt.setText(it.createdAt)
            bind.tvCreatedBy.setText(it.createdBy)
            val formatTotal = DecimalFormat("###,###.##").format(it.order?.total)
            bind.tvTotal.setText(formatTotal)
            bind.tvTienGiam.setText(it.discount.toString() + it.discountType)
            val formatTongThu = DecimalFormat("###,###.##").format(it.total)
            bind.tvTongThu.setText(formatTongThu)
            bind.tvPhuongthucthanhtoan.setText(it.paymentMethod)
        }
    }

    override fun onBillListLoaded(dataList: List<Bill>) {
    }

    override fun onBarEntryListLoaded(dataList: List<BarEntry>) {
    }

    override fun onBillActionSuccess(message: String) {
    }

    override fun onBillActionError(error: String) {
    }
}