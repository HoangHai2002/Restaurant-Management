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
import com.example.restaurant_management.databinding.ActivityEditUserBinding

class EditUserActivity : AppCompatActivity(), IUserView {
    private lateinit var bind: ActivityEditUserBinding
    private lateinit var controller: UserController
    private lateinit var user : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityEditUserBinding.inflate(layoutInflater)
        setContentView(bind.root)


        // Setup toolbar
        setSupportActionBar(bind.toolbar3)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.toolbar3.setTitle("Chỉnh sửa thông tin")

        val userId = intent.getStringExtra("userId")
        controller = UserController(this)
        controller.getById(userId!!)

        // Setup menu role
        val options = arrayOf("Admin", "Thu ngân", "Phục vụ","Bếp")
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        bind.spinner.adapter = arrayAdapter

        var role = -1
        bind.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                role = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        bind.btnEdit.setOnClickListener {
            if (bind.edtFullname.text.toString() == "") bind.edtFullname.error = "Vui lòng nhập thông tin"
            if (bind.edtUsername.text.toString() == "") bind.edtUsername.error = "Vui lòng nhập thông tin"
            if (bind.edtPassword.text.toString() == "") bind.edtPassword.error = "Vui lòng nhập thông tin"

            val fullname = bind.edtFullname.text.toString()
            val username = bind.edtUsername.text.toString()
            val password = bind.edtPassword.text.toString()

            if(fullname != "" && username != "" && password != "" && checkChageValue(fullname,username, password, role)){
                val newUser = User(user.id, fullname, username, password, role)
                controller.update(userId, newUser)
            }
            if (!checkChageValue(fullname,username, password, role)){
                Toast.makeText(this, "Dữ liệu chưa được thay đổi", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // true == change
    private fun checkChageValue(fullname : String, username : String, password :String, role : Int): Boolean {
        if(user.fullName != fullname || user.userName != username ||
            user.passWord != password || user.role != role){
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                finish()
                true
            }
            else -> false
        }
    }

    override fun onUserDataLoaded(data: User?) {
        if (data != null){
            bind.edtFullname.setText(data.fullName)
            bind.edtUsername.setText(data.userName)
            bind.edtPassword.setText(data.passWord)
            bind.spinner.setSelection(data.role!!)

            user = data
        }
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