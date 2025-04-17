package com.example.restaurant_management.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.Adapter.IClickTableAreaItem
import com.example.restaurant_management.Adapter.TableAreaManagementAdapter
import com.example.restaurant_management.Controller.ITableAreaView
import com.example.restaurant_management.Controller.TableAreaController
import com.example.restaurant_management.Models.TableArea
import com.example.restaurant_management.databinding.ActivityTableAreaManagementBinding


class TableAreaManagementActivity : AppCompatActivity(),ITableAreaView{
    private lateinit var bind : ActivityTableAreaManagementBinding
    private lateinit var listTableArea: MutableList<TableArea>
    private lateinit var adapter : TableAreaManagementAdapter
    private  var controller = TableAreaController(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityTableAreaManagementBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setSupportActionBar(bind.toolbar4)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.toolbar4.setTitle("Quản lý khu vực bàn")
        val listener = object : IClickTableAreaItem{
            override fun onUpdate(tableArea: TableArea, position: Int) {
                val builder = AlertDialog.Builder(this@TableAreaManagementActivity)
                builder.setTitle("Chỉnh sửa thông tin")
                val tableAreaName = EditText(this@TableAreaManagementActivity)
                tableAreaName.setText(tableArea.tableAreaName)

                val layout = LinearLayout(this@TableAreaManagementActivity)
                layout.orientation  = LinearLayout.VERTICAL
                layout.setPadding(50,40,50,10)
                layout.addView(tableAreaName)
                builder.setView(layout)
                builder.setPositiveButton("Xác nhận"){dialog, which ->
                    val name = tableAreaName.text.toString().trim()
                   if (name == tableArea.tableAreaName){
                       Toast.makeText(this@TableAreaManagementActivity, "Thông tin chưa được chỉnh sửa", Toast.LENGTH_SHORT).show()
                   }else{
                       val newTableArea = TableArea(tableArea.id, name)
                       controller.update(tableArea.id!!, newTableArea)
                   }
                }
                builder.setNegativeButton("Hủy"){dialog, which->
                    dialog.dismiss()
                }
                builder.show()
            }

            override fun onDelete(tableArea: TableArea, position: Int) {
                AlertDialog.Builder(this@TableAreaManagementActivity)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa khu vực này?")
                    .setPositiveButton("Xóa"){dialog, which -> controller.delete(tableArea.id!!)}
                    .setNegativeButton("Hủy"){dialog, which -> dialog.dismiss()}
                    .show()
            }
        }

        listTableArea = mutableListOf()
        adapter = TableAreaManagementAdapter(listTableArea, listener)
        bind.rcTableAreaManagement.layoutManager = GridLayoutManager(this, 2,RecyclerView.VERTICAL, false)
        bind.rcTableAreaManagement.adapter = adapter

        // Call Data GetAll TableArea
        controller.getAll()

        // Add TableArea
        bind.btnAdd.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Thêm khu vực")
            val tableAreaName = EditText(this)
            tableAreaName.hint = "Nhập tên khu vực"

            val layout =LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(50, 40, 50, 10)
            layout.addView(tableAreaName)
            builder.setView(layout)
            builder.setPositiveButton("Thêm"){dialog, which->
                val name = tableAreaName.text.toString().trim()
                if (name.isEmpty()){
                    Toast.makeText(this, "Tên khu vực không được để trống", Toast.LENGTH_SHORT).show()
                }else{
                   controller.add(TableArea("1", name))
                    dialog.dismiss()
                }
            }
            builder.setNegativeButton("Hủy"){dialog, which ->
                dialog.dismiss()
            }
            builder.show()

        }

    }

    override fun onResume() {
        super.onResume()
        controller.getAll()
    }

    override fun onTableAreaDataLoaded(data: TableArea?) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTableAreaDataListLoaded(dataList: List<TableArea>) {
        listTableArea.clear()
        listTableArea.addAll(dataList)
        adapter.notifyDataSetChanged()
    }

    override fun onTableAreaActionSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        controller.getAll()
    }

    override fun onTableAreaActionError(error: String) {
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