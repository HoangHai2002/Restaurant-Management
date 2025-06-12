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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant_management.Adapter.DishCategoryAdapter
import com.example.restaurant_management.Adapter.IClickDishCategoryItem
import com.example.restaurant_management.Controller.DishCategoryController
import com.example.restaurant_management.Controller.IDishCategoryView
import com.example.restaurant_management.Models.DishCategory
import com.example.restaurant_management.Models.TableArea
import com.example.restaurant_management.databinding.ActivityDishCategoryManagementBinding

class DishCategoryManagementActivity : AppCompatActivity(), IDishCategoryView {
    lateinit var bind : ActivityDishCategoryManagementBinding
    lateinit var adapter : DishCategoryAdapter
    lateinit var lishDishCategory: MutableList<DishCategory>
    lateinit var controller: DishCategoryController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityDishCategoryManagementBinding.inflate(layoutInflater)
        setContentView(bind.root)
        controller = DishCategoryController(this)

        setSupportActionBar(bind.toolbar4)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.toolbar4.setTitle("Quản lý loại món ăn")

        val listener = object : IClickDishCategoryItem{
            override fun onUpdate(dishCategory: DishCategory, pos: Int) {
                showDialogUpdate(dishCategory)
            }

            override fun onDelete(dishCategory: DishCategory, pos: Int) {
                showDialogDelete(dishCategory)
            }
        }

        lishDishCategory = mutableListOf()
        adapter = DishCategoryAdapter(lishDishCategory, listener)
        bind.rcListDishCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.rcListDishCategory.adapter = adapter
        controller.getAll()

        bind.imvAdd.setOnClickListener {
            showDialogAdd()
        }
    }

    fun showDialogDelete(data : DishCategory){
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa loại món ăn này?")
            .setPositiveButton("Xóa"){dialog, which -> controller.delete(data.id!!)}
            .setNegativeButton("Hủy"){dialog, which -> dialog.dismiss()}
            .show()
    }
    fun showDialogUpdate(data : DishCategory){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Chỉnh sửa thông tin")
        val tableAreaName = EditText(this)
        tableAreaName.setText(data.dishCategoryName)

        val layout = LinearLayout(this)
        layout.orientation  = LinearLayout.VERTICAL
        layout.setPadding(50,40,50,10)
        layout.addView(tableAreaName)
        builder.setView(layout)
        builder.setPositiveButton("Xác nhận"){dialog, which ->
            val name = tableAreaName.text.toString().trim()
            if (name == data.dishCategoryName){
                Toast.makeText(this, "Thông tin chưa được chỉnh sửa", Toast.LENGTH_SHORT).show()
            }else{
                val newDishCategory = DishCategory(data.id, name)
                controller.update(data.id!!, newDishCategory)
            }
        }
        builder.setNegativeButton("Hủy"){dialog, which->
            dialog.dismiss()
        }
        builder.show()
    }

    fun showDialogAdd(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thêm loại món ăn")
        val dishCategoryName = EditText(this)
        dishCategoryName.hint = "Nhập tên loại món ăn"

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)
        layout.addView(dishCategoryName)
        builder.setView(layout)
        builder.setPositiveButton("Thêm"){dialog, which->
            val name = dishCategoryName.text.toString().trim()
            if (name.isEmpty()){
                Toast.makeText(this, "Tên loại món ăn không được để trống", Toast.LENGTH_SHORT).show()
            }else{
                controller.add(DishCategory("1", name))
                dialog.dismiss()
            }
        }
        builder.setNegativeButton("Hủy"){dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }
    override fun onDishCategoryDataLoaded(data: DishCategory?) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDishCategoryDataListLoaded(dataList: List<DishCategory>) {
        lishDishCategory.clear()
        lishDishCategory.addAll(dataList)
        adapter.notifyDataSetChanged()
    }

    override fun onDishCategoryActionSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        controller.getAll()
    }

    override fun onDishCategoryActionError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
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