package com.example.restaurant_management.View

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurant_management.Controller.BillController
import com.example.restaurant_management.Controller.IBillView
import com.example.restaurant_management.Models.Bill
import com.example.restaurant_management.databinding.ActivityRevenueMainBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class RevenueActivity : AppCompatActivity(), IBillView {
    lateinit var bind: ActivityRevenueMainBinding
    lateinit var billController: BillController
    @RequiresApi(Build.VERSION_CODES.O)
    var today = LocalDate.now()
    @RequiresApi(Build.VERSION_CODES.O)
    var select = today
    @RequiresApi(Build.VERSION_CODES.O)
    var type = "week"
    lateinit var monday :LocalDate
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityRevenueMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        billController= BillController(this)
        setSupportActionBar(bind.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.toolbar.title = "Quản lý doanh thu"

        // Get all day of week
        monday = today.with(DayOfWeek.MONDAY)
        var week = (0..6).map { monday.plusDays(it.toLong()) }
        revenueWeek(week)
        bind.btnLastWeek.setOnClickListener {
            when(type){
                "week" ->{
                    monday = monday.minusWeeks(1)
                    week = (0..6).map { monday.plusDays(it.toLong())}
                    revenueWeek(week)
                }
                "month" ->{
                    select = select.minusMonths(1)
                    val lastYearMonth = YearMonth.from(select)
                    val daysInMonth = lastYearMonth.lengthOfMonth()
                    val allDatesInMonth = (1..daysInMonth).map { day ->
                        LocalDate.of(lastYearMonth.year, lastYearMonth.month, day)
                    }
                    revenueWeek(allDatesInMonth)
                }
                "year" ->{
                    select = select.minusYears(1)
                    val monthInYear = (1..12).map {month ->
                        YearMonth.of(select.year, month)
                    }
                    revenueYear(monthInYear)
                }
            }

        }
        bind.btnNextWeek.setOnClickListener {
            when(type){
                "week" ->{
                    monday = monday.plusWeeks(1)
                    week = (0..6).map { monday.plusDays(it.toLong())}
                    revenueWeek(week)
                }
                "month" ->{
                    select = select.plusMonths(1)
                    val lastYearMonth = YearMonth.from(select)
                    val daysInMonth = lastYearMonth.lengthOfMonth()
                    val allDatesInMonth = (1..daysInMonth).map { day ->
                        LocalDate.of(lastYearMonth.year, lastYearMonth.month, day)
                    }
                    revenueWeek(allDatesInMonth)
                }
                "year" ->{
                    select = select.plusYears(1)
                    val monthInYear = (1..12).map {month ->
                        YearMonth.of(select.year, month)
                    }
                    revenueYear(monthInYear)
                }
            }
        }
        bind.btnMenu.setOnClickListener {
            showMenu()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showMenu() {
        val popupmenu = PopupMenu(this, bind.btnMenu)
        popupmenu.menu.add("Doanh thu theo tuần")
        popupmenu.menu.add("Doanh thu theo tháng")
        popupmenu.menu.add("Doanh thu theo năm")
        popupmenu.menu.add("Lịch sử thanh toán")


        popupmenu.setOnMenuItemClickListener {  menuItem ->
            when(menuItem.title.toString()){
                "Doanh thu theo tuần" ->{
                    type = "week"
                    bind.btnLastWeek.setText("Tuần trước")
                    bind.btnNextWeek.setText("Tuần tiếp")
                    monday = today.with(DayOfWeek.MONDAY)
                    val week = (0..6).map { monday.plusDays(it.toLong()) }
                    revenueWeek(week)
                    true
                }
                "Doanh thu theo tháng" ->{
                    select = today
                    type = "month"
                    bind.btnLastWeek.setText("Tháng trước")
                    bind.btnNextWeek.setText("Tháng tiếp")

                    val currentYearMonth = YearMonth.from(today)
                    val daysInMonth = currentYearMonth.lengthOfMonth() // Số ngày trong tháng

                    val allDatesInMonth = (1..daysInMonth).map { day ->
                        LocalDate.of(today.year, today.month, day)
                    }
                    revenueWeek(allDatesInMonth)
                    true
                }
                "Doanh thu theo năm" ->{
                    type = "year"
                    select = today
                    bind.btnLastWeek.setText("Năm trước")
                    bind.btnNextWeek.setText("Năm tiếp")

                    val currentYear = today.year
                    val monthInYear = (1..12).map {month ->
                        YearMonth.of(currentYear, month)
                    }
                    revenueYear(monthInYear)
                    true
                }
                "Lịch sử thanh toán" ->{
                   val intent = Intent(this, PaymentHistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popupmenu.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun revenueWeek(week: List<LocalDate>){

        val formatter = DateTimeFormatter.ofPattern("dd/MM")
        billController.getRevenueByWeek(week)

        bind.barChart.description.isEnabled = false
        val xAxis = bind.barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.labelRotationAngle = -45f
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < week.size) week[index].format(formatter) else ""
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun revenueYear(year: List<YearMonth>){
        val formatter = DateTimeFormatter.ofPattern("MM/yyyy")
        billController.getRevenueByYear(year)

        bind.barChart.description.isEnabled = false
        val xAxis = bind.barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.labelRotationAngle = -45f
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index in year.indices) year[index].format(formatter) else ""
            }
        }

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

    override fun onBillDataLoaded(data: Bill?) {
        TODO("Not yet implemented")
    }

    override fun onBarEntryListLoaded(dataList: List<BarEntry>) {
        val entries = dataList

        val dataSet = BarDataSet(entries, "Doanh thu (VNĐ)")
        dataSet.color = Color.BLUE

        val data = BarData(dataSet)
        data.barWidth = 0.9f

        bind.barChart.data = data
        bind.barChart.setFitBars(true)
        bind.barChart.invalidate()
    }

    override fun onBillListLoaded(dataList: List<Bill>) {
        TODO("Not yet implemented")
    }

    override fun onBillActionSuccess(message: String) {
        TODO("Not yet implemented")
    }

    override fun onBillActionError(error: String) {
        TODO("Not yet implemented")
    }
}