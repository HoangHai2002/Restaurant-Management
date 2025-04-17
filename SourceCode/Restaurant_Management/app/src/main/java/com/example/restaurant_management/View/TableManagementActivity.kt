package com.example.restaurant_management.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant_management.Adapter.IClickTableItem
import com.example.restaurant_management.Adapter.TableManagementAdapter
import com.example.restaurant_management.Controller.ITableAreaView
import com.example.restaurant_management.Controller.ITableView
import com.example.restaurant_management.Controller.TableAreaController
import com.example.restaurant_management.Controller.TableController
import com.example.restaurant_management.Models.Table
import com.example.restaurant_management.Models.TableArea
import com.example.restaurant_management.databinding.ActivityTableManagementBinding

class TableManagementActivity : AppCompatActivity(), ITableView, ITableAreaView {
    private lateinit var bind: ActivityTableManagementBinding
    private lateinit var adapter: TableManagementAdapter
    private lateinit var listTable: MutableList<Table>
    private lateinit var tableController: TableController
    private lateinit var tableAreaController: TableAreaController
    private lateinit var tableArea: TableArea
//    private lateinit var listTableArea: MutableList<TableArea>
    private lateinit var options: MutableList<TableArea>
    private var updateTableAreaId : String? = ""
    private lateinit var spinnerUpdate : Spinner
    private lateinit var tableAreaOptions : MutableList<TableArea>
    private lateinit var fullListTable : MutableList<Table>

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityTableManagementBinding.inflate(layoutInflater)
        setContentView(bind.root)
        tableAreaController = TableAreaController(this)
        tableController = TableController(this)
        options = mutableListOf()

        // Toolbar
        setSupportActionBar(bind.toolbar5)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.toolbar5.setTitle("Quản lý bàn")

        // Menu click Table item
        val listener = object : IClickTableItem {
            override fun onUpdate(table: Table, position: Int) {
                updateTableAreaId = table.tableAreaId
                showDialogUpdate((table))
            }

            override fun onDelete(table: Table, position: Int) {
                showDialogDelete(table)
            }

        }

        // Get data Table
        listTable = mutableListOf()
        adapter = TableManagementAdapter(listTable, listener)
        bind.rcListTable.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.rcListTable.adapter = adapter
        tableController.getAll()

        // Add Table
        bind.btnAdd.setOnClickListener {
            showDialogAdd()
        }

        // Find Table
        tableAreaController.getAll()
        tableAreaOptions = mutableListOf(TableArea("0", "Tất cả"))
        val arrayAdapterTableArea = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tableAreaOptions)
        bind.spinnerTableArea.adapter = arrayAdapterTableArea
        var tableAreaFind = TableArea("0", "Tất cả")
        bind.spinnerTableArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                tableAreaFind = p0?.getItemAtPosition(p2) as TableArea
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        val statusOptions = mutableListOf("Tất cả", "Trống", "Đang dùng", "Đặt trước")
        val arrayAdapterStatus = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusOptions)
        bind.spinnerStatus.adapter = arrayAdapterStatus
        var statusFind = "Tất cả"
        bind.spinnerStatus.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                statusFind = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        bind.tvLoc.setOnClickListener {
            findTable(tableAreaFind, statusFind)
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun findTable(tableAreaFind : TableArea, statusFind : String){
        when(statusFind){
            "Tất cả" ->{
                // Find By TableArea
                if (tableAreaFind.id == "0"){
                    listTable.clear()
                    listTable.addAll(fullListTable)
                    adapter.notifyDataSetChanged()
                } else{
                    val filterList = fullListTable.filter { it.tableAreaId == tableAreaFind.id }.toMutableList()
                    listTable.clear()
                    listTable.addAll(filterList)
                    adapter.notifyDataSetChanged()
                }
            }
            "Trống" ->{
                // Find by status "Trống"
                if (tableAreaFind.id == "0"){
                    val filterList = fullListTable.filter { it.status == "Trống" }
                    listTable.clear()
                    listTable.addAll(filterList)
                    adapter.notifyDataSetChanged()
                } else{
                    val filterList = fullListTable.filter { it.status == "Trống" && it.tableAreaId == tableAreaFind.id }
                    listTable.clear()
                    listTable.addAll(filterList)
                    adapter.notifyDataSetChanged()
                }
            }

            "Đang dùng" ->{
                // Find by status "Trống"
                if (tableAreaFind.id == "0"){
                    val filterList = fullListTable.filter { it.status == "Đang dùng" }
                    listTable.clear()
                    listTable.addAll(filterList)
                    adapter.notifyDataSetChanged()
                } else{
                    val filterList = fullListTable.filter { it.status == "Đang dùng" && it.tableAreaId == tableAreaFind.id }
                    listTable.clear()
                    listTable.addAll(filterList)
                    adapter.notifyDataSetChanged()
                }
            }

            "Đặt trước" ->{
                // Find by status "Trống"
                if (tableAreaFind.id == "0"){
                    val filterList = fullListTable.filter { it.status == "Đặt trước" }
                    listTable.clear()
                    listTable.addAll(filterList)
                    adapter.notifyDataSetChanged()
                } else{
                    val filterList = fullListTable.filter { it.status == "Đặt trước" && it.tableAreaId == tableAreaFind.id }
                    listTable.clear()
                    listTable.addAll(filterList)
                    adapter.notifyDataSetChanged()
                }
            }


        }
    }
    fun showDialogDelete(table: Table){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Xác nhận xóa")
        if (table.status != "Trống"){
            builder.setMessage("Không thể xóa bàn đang sử dụng")
            builder.setNegativeButton("Hủy"){dialog, which -> dialog.dismiss()}
        } else{
            builder.setMessage("Bạn có chắc muốn xóa bàn này?")
            builder.setPositiveButton("Xóa"){dialog, which -> tableController.delete(table.id!!)}
            builder.setNegativeButton("Hủy"){dialog, which -> dialog.dismiss()}
        }

        builder.show()
    }

    fun showDialogUpdate(table: Table){
        val builder = AlertDialog.Builder(this@TableManagementActivity)
        builder.setTitle("Chỉnh sửa thông tin")
        val tableName = EditText(this@TableManagementActivity)
        tableName.setText(table.tableName)
        spinnerUpdate = Spinner(this@TableManagementActivity)

        tableAreaController.getAll()
//        listTableArea = mutableListOf()
        options = mutableListOf(TableArea("0", "Chon khu vực"))

        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        spinnerUpdate.adapter = arrayAdapter
        spinnerUpdate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                tableArea = p0?.getItemAtPosition(p2) as TableArea
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }


        val layout = LinearLayout(this@TableManagementActivity)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)
        layout.addView(tableName)
        layout.addView(spinnerUpdate)
        builder.setView(layout)
        builder.setPositiveButton("Xác nhận") { dialog, which ->
            val name = tableName.text.toString().trim()
            if (tableArea.id == "0"){
                Toast.makeText(this, "Vui lòng chọn khu vực", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            if (name == table.tableName && tableArea.id == table.tableAreaId) {
                Toast.makeText(
                    this@TableManagementActivity,
                    "Thông tin chưa được chỉnh sửa",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val newTable = Table(
                    table.id,
                    tableArea.id,
                    tableArea.tableAreaName,
                    tableName.text.toString()
                )
                tableController.update(table.id!!, newTable)
            }
        }
        builder.setNegativeButton("Hủy") { dialog, which ->
            dialog.dismiss()
        }
        Log.d("3", options.size.toString())
        builder.show()
    }

    fun showDialogAdd() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thêm khu vực")
        val tableName = EditText(this)
        tableName.hint = "Nhập tên bàn"
        val spinner = Spinner(this)
        tableAreaController.getAll()
//        listTableArea = mutableListOf()
        options = mutableListOf(TableArea("0", "Chon khu vực"))

        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                tableArea = p0?.getItemAtPosition(p2) as TableArea
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)
        layout.addView(tableName)
        layout.addView(spinner)
        builder.setView(layout)
        builder.setPositiveButton("Thêm") { dialog, which ->
            if (tableName.text.toString() == "") {
                Toast.makeText(this, "Tên bàn không được để trống", Toast.LENGTH_SHORT).show()
            } else if (tableArea.id == "0") {
                Toast.makeText(this, "Vui lòng chọn khu vực", Toast.LENGTH_SHORT).show()
            } else {
                val table =
                    Table("", tableArea.id, tableArea.tableAreaName, tableName.text.toString())
                tableController.add(table)
                dialog.dismiss()
            }

        }
        builder.setNegativeButton("Hủy") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    // TableArea
    override fun onTableAreaDataLoaded(data: TableArea?) {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTableAreaDataListLoaded(dataList: List<TableArea>) {
//        listTableArea.clear()
//        listTableArea.addAll(dataList)
        options.addAll(dataList)
        tableAreaOptions.clear()
        tableAreaOptions.add(TableArea("0", "Tất cả"))
        tableAreaOptions.addAll(dataList)

        val selectedIndex = options.indexOfFirst { it.id == updateTableAreaId }
        if (selectedIndex >= 0) {
            spinnerUpdate.setSelection(selectedIndex)
        }
    }

    override fun onTableAreaActionSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onTableAreaActionError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }


    // Table
    override fun onTableDataLoaded(data: Table?) {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTableDataListLoaded(dataList: List<Table>) {
        listTable.clear()
        listTable.addAll(dataList)
        fullListTable = mutableListOf<Table>()
        fullListTable.addAll(listTable)
        adapter.notifyDataSetChanged()
    }

    override fun onTableActionSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        tableController.getAll()

    }

    override fun onTableActionError(error: String) {
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
}