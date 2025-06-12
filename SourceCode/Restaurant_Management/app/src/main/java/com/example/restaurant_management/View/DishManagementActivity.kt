package com.example.restaurant_management.View

import android.annotation.SuppressLint
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
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
import com.example.restaurant_management.Adapter.DishManagementAdapter
import com.example.restaurant_management.Adapter.IClickDishItem
import com.example.restaurant_management.Controller.DishCategoryController
import com.example.restaurant_management.Controller.DishController
import com.example.restaurant_management.Controller.IDishCategoryView
import com.example.restaurant_management.Controller.IDishView
import com.example.restaurant_management.Models.Dish
import com.example.restaurant_management.Models.DishCategory
import com.example.restaurant_management.databinding.ActivityDishManagementBinding

class DishManagementActivity : AppCompatActivity(), IDishView, IDishCategoryView  {
    private lateinit var bind : ActivityDishManagementBinding
    private lateinit var listDish: MutableList<Dish>
    private lateinit var adapter : DishManagementAdapter
    private lateinit var dishController: DishController
    private lateinit var dishCategoryController: DishCategoryController
    private lateinit var options: MutableList<DishCategory>
    private lateinit var dishCategory: DishCategory
    private lateinit var spinnerUpdate: Spinner
    private var dishCategoryId: String? = ""
    private lateinit var fullListDish: MutableList<Dish>
    var keyword = ""
    var dishCategorySelected = DishCategory("0", "Tất cả")
    var sortedList = mutableListOf<Dish>()
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityDishManagementBinding.inflate(layoutInflater)
        setContentView(bind.root)
        dishController = DishController(this)
        dishCategoryController = DishCategoryController(this)
        fullListDish = mutableListOf<Dish>()

        setSupportActionBar(bind.toolbar6)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.toolbar6.setTitle("Quản lý món ăn")

        val listener = object : IClickDishItem {
            override fun onUpdate(dish: Dish, pos: Int) {
                dishCategoryId = dish.dishCategoryId
                showDialogUpdate(dish)
            }

            override fun onDelete(dish: Dish, pos: Int) {
                showDialogDelete(dish)
            }

            override fun onChangeQuantity(dish: Dish, pos: Int) {

            }

        }
        listDish = mutableListOf()
        adapter = DishManagementAdapter(listDish, listener, "Dish")
        bind.rcListDish.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.rcListDish.adapter = adapter
        dishController.getAll()

        bind.btnAdd.setOnClickListener {
            showDialogAdd()
        }

        //Search
        bind.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun afterTextChanged(p0: Editable?) {
                keyword = p0.toString().trim().lowercase()
                if (keyword == "" && dishCategorySelected.id == "0") {
                    dishController.getAll()
                }
                sortedList = listDish.sortedWith(compareByDescending<Dish> {
                    it.dishName?.lowercase()?.startsWith(keyword) == true
                }.thenByDescending {
                    it.dishName?.lowercase()?.contains(keyword) == true
                }).toMutableList()

                listDish.clear()
                listDish.addAll(sortedList)
                adapter.notifyDataSetChanged()
            }
        })


        //Find
        dishCategoryController.getAll()
        options = mutableListOf(DishCategory("0", "Tất cả"))
        val arrayAdapterTableArea =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        bind.spinnerDishCategoryName.adapter = arrayAdapterTableArea

        bind.spinnerDishCategoryName.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    dishCategorySelected = p0?.getItemAtPosition(p2) as DishCategory
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

        bind.btnFind.setOnClickListener {
            val filteredList = fullListDish.filter { dish ->
                val matchKeyword =
                    keyword.isBlank() || dish.dishName?.lowercase()?.contains(keyword) == true
                val matchCategory =
                    dishCategorySelected.id == "0" || dish.dishCategoryId == dishCategorySelected.id
                matchKeyword && matchCategory
            }.sortedWith(
                compareByDescending<Dish> {
                    it.dishName?.lowercase()?.startsWith(keyword) == true
                }.thenByDescending {
                    it.dishName?.lowercase()?.contains(keyword) == true
                }
            )

            listDish.clear()
            listDish.addAll(filteredList)
            adapter.notifyDataSetChanged()
        }
    }

    fun showDialogUpdate(dish: Dish) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Chỉnh sửa thông tin")
        val edtDishName = EditText(this)
        edtDishName.setText(dish.dishName)
        val edtDescription = EditText(this)
        edtDescription.setText(dish.description)
        val edtPrice = EditText(this)
        val formatprice = DecimalFormat("###.##").format( dish.price)
        edtPrice.setText(formatprice)
        edtPrice.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        spinnerUpdate = Spinner(this)

        dishCategoryController.getAll()
        options = mutableListOf(DishCategory("0", "Chon loại món ăn"))

        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        spinnerUpdate.adapter = arrayAdapter
        spinnerUpdate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                dishCategory = p0?.getItemAtPosition(p2) as DishCategory
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)
        layout.addView(edtDishName)
        layout.addView(edtDescription)
        layout.addView(spinnerUpdate)
        layout.addView(edtPrice)
        builder.setView(layout)
        builder.setPositiveButton("Xác nhận") { dialog, which ->
            val name = edtDishName.text.toString().trim()
            val description = edtDescription.text.toString().trim()
            val price = edtPrice.text.toString().trim().toDouble()
            if (dishCategory.id == "0") {
                Toast.makeText(this, "Vui lòng chọn loại món ăn", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            if (name == dish.dishName && dishCategory.id == dish.dishCategoryId && description == dish.description && price == dish.price) {
                Toast.makeText(
                    this,
                    "Thông tin chưa được chỉnh sửa",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val newDish = Dish(dish.id, dishCategory.id, name, dishCategory.dishCategoryName, description, price,true)
                dishController.update(dish.id!!, newDish)
            }
        }
        builder.setNegativeButton("Hủy") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    fun showDialogDelete(dish: Dish) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Xác nhận xóa")
        builder.setMessage("Bạn có chắc muốn xóa món ăn này?")
        builder.setPositiveButton("Xóa") { dialog, which -> dishController.delete(dish.id!!) }
        builder.setNegativeButton("Hủy") { dialog, which -> dialog.dismiss() }
        builder.show()
    }
    fun showDialogAdd() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thêm món ăn")
        val dishName = EditText(this)
        dishName.hint = "Nhập tên món ăn"
        val description = EditText(this)
        description.hint = "Nhập mô tả món ăn"
        val price = EditText(this)
        price.hint = "Nhập giá"
        price.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        val spinner = Spinner(this)
        dishCategoryController.getAll()
        options = mutableListOf(DishCategory("0", "Chon loại món ăn"))

        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                dishCategory = p0?.getItemAtPosition(p2) as DishCategory
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)
        layout.addView(dishName)
        layout.addView(description)
        layout.addView(price)
        layout.addView(spinner)
        builder.setView(layout)
        builder.setPositiveButton("Thêm") { dialog, which ->
            if (dishName.text.toString() == "") {
                Toast.makeText(this, "Tên bàn không được để trống", Toast.LENGTH_SHORT).show()
            } else if (dishCategory.id == "0") {
                Toast.makeText(this, "Vui lòng chọn khu vực", Toast.LENGTH_SHORT).show()
            } else if(price.text.toString() == ""){
                Toast.makeText(this, "Giá không được để trống", Toast.LENGTH_SHORT).show()
            }else{
                val id =""
                val dishCategoryId = dishCategory.id
                val name = dishName.text.toString()
                val dishCategoryName = dishCategory.dishCategoryName
                val des = description.text.toString()
                val dishPrice =  price.text.toString().toDouble()
                val dish =
                    Dish(id, dishCategoryId, name, dishCategoryName, des, dishPrice, true )
                dishController.add(dish)
                dialog.dismiss()
            }

        }
        builder.setNegativeButton("Hủy") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onDishDataLoaded(data: Dish?) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDishDataListLoaded(dataList: List<Dish>) {
        listDish.clear()
        listDish.addAll(dataList)
        fullListDish.clear()
        fullListDish.addAll(listDish)
        adapter.notifyDataSetChanged()
    }

    override fun onDishActionSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        dishController.getAll()
    }

    override fun onDishActionError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }


    // DishCategory
    override fun onDishCategoryDataLoaded(data: DishCategory?) {
    }

    override fun onDishCategoryDataListLoaded(dataList: List<DishCategory>) {
        options.addAll(dataList)
        val selectedIndex = options.indexOfFirst { it.id == dishCategoryId }
        if (selectedIndex >= 0) {
            spinnerUpdate.setSelection(selectedIndex)
        }
        Log.d("POS", selectedIndex.toString())


    }

    override fun onDishCategoryActionSuccess(message: String) {
    }

    override fun onDishCategoryActionError(error: String) {

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