package com.example.restaurant_management.View

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurant_management.Controller.IUserView
import com.example.restaurant_management.Controller.UserController
import com.example.restaurant_management.Models.User
import com.example.restaurant_management.databinding.ActivityAddUserBinding

class AddUserActivity : AppCompatActivity(), IUserView {
    private lateinit var bind : ActivityAddUserBinding
    private  lateinit var controller : UserController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(bind.root)

        controller = UserController(this)

        setSupportActionBar(bind.toolbar2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.toolbar2.setTitle("Thêm nhân viên")

        // Set option dropdown menu choose role
        val options = arrayOf("Admin", "Thu ngân", "Phục vụ","Bếp")
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        bind.spinner.adapter = arrayAdapter

        var role = 0
        bind.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                role = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        
        bind.btnAdd.setOnClickListener { 
            if (bind.edtFullname.text.toString() == "") bind.edtFullname.error = "Vui lòng nhập thông tin"
            if (bind.edtUsername.text.toString() == "") bind.edtUsername.error = "Vui lòng nhập thông tin"
            if (bind.edtPassword.text.toString() == "") bind.edtPassword.error = "Vui lòng nhập thông tin"
            if(bind.edtFullname.text.toString() != "" &&
                bind.edtUsername.text.toString() != "" &&
                bind.edtPassword.text.toString() != ""){

                val fullname = bind.edtFullname.text.toString()
                val username = bind.edtUsername.text.toString()
                val password = bind.edtPassword.text.toString()
                val user = User("", fullname, username, password, role)
                controller.add(user)
            }

        }
    }

//    override fun onDataLoaded(data: User?) {
//    }
//
//    override fun onDataListLoaded(dataList: List<User>) {
//    }
//
//    override fun onActionSuccess(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//        finish()
//    }
//
//    override fun onActionError(error: String) {
//        when(error){
//            "Tên người dùng đã tồn tại" -> bind.edtFullname.error = error
//            "Tên đăng nhập đã tồn tại" -> bind.edtUsername.error = error
//            else -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
//        }
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onUserDataLoaded(data: User?) {
        TODO("Not yet implemented")
    }

    override fun onUserDataListLoaded(dataList: List<User>) {
        TODO("Not yet implemented")
    }

    override fun onUserActionSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onUserActionError(error: String) {
        when(error){
            "Tên người dùng đã tồn tại" -> bind.edtFullname.error = error
            "Tên đăng nhập đã tồn tại" -> bind.edtUsername.error = error
            else -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }
}