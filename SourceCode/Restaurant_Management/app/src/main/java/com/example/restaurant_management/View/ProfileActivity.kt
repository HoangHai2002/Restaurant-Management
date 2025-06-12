package com.example.restaurant_management.View

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurant_management.Controller.IUserView
import com.example.restaurant_management.Controller.UserController
import com.example.restaurant_management.Models.User
import com.example.restaurant_management.Repository.Preferences
import com.example.restaurant_management.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity(), IUserView {
    lateinit var bind: ActivityProfileBinding
    lateinit var user: User
    private lateinit var controller: UserController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // SetUp Toolbar
        setSupportActionBar(bind.toolbar1)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Thông tin cá nhân"

        // Call data
        controller = UserController(this)
        val preferences = Preferences(this)
        val id = preferences.getId().toString()
        controller.getById(id)

        bind.btnSave.setOnClickListener {
            val fullname = bind.edtFullname.text.toString().trim()
            val username = bind.edtUsername.text.toString().trim()
            if (fullname != "" && username != ""){
                if(fullname == user.fullName && username == user.userName){
                    Toast.makeText(this, "Thông tin chưa được chỉnh sửa", Toast.LENGTH_SHORT).show()
                }else{
                    var newUser = user
                    newUser.fullName = fullname
                    newUser.userName = username
                    user.id?.let { it1 -> controller.update(it1, newUser) }

                }
            }else{
                if (fullname == ""){
                    bind.edtFullname.error = "Vui lòng nhập thông tin"
                }
                if (username == ""){
                    bind.edtUsername.error = "Vui lòng nhập thông tin"
                }
            }

        }

        bind.btnChangePassword.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Đổi mật khẩu")
            val edtpassword = EditText(this)
            edtpassword.hint = "Nhập mật khẩu"
            val edtnewpassword = EditText(this)
            edtnewpassword.hint = "Nhập mật khẩu mới: >=6 ký tự(A-Z, a-z, 0-9)"
            val edtconfirmpassword = EditText(this)
            edtconfirmpassword.hint = "Xác nhận mật khẩu"

            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(50,40,50,10)
            layout.addView(edtpassword)
            layout.addView(edtnewpassword)
            layout.addView(edtconfirmpassword)
            builder.setView(layout)
            builder.setPositiveButton("OK"){dialog, which ->
                val password = edtpassword.text.toString().trim()
                val newpassword = edtnewpassword.text.toString().trim()
                val confirmpassword = edtconfirmpassword.text.toString().trim()
                if (password != "" && newpassword != "" && confirmpassword != ""){
                    if (password == user.passWord){
                        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$").matches(newpassword)
                        if(regex) {
                            if (newpassword == confirmpassword) {
                                val newUser = user
                                newUser.passWord = newpassword
                                user.id?.let { it1 -> controller.update(it1, newUser) }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Xác nhận mật khẩu không trùng khớp",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }else{
                            Toast.makeText(this, "Mật khẩu không đúng định dạng", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, "Mật khẩu không chính xác", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "Vui lòng nhập thông tin", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Hủy"){dialog, which -> dialog.dismiss()}
            builder.show()
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onUserDataLoaded(data: User?) {
        data?.let {
            user = data

            bind.edtFullname.setText(data.fullName)
            bind.edtUsername.setText(data.userName)
            bind.tvRole.setText(data.convertRoleToText(data.role!!))
        }
    }

    override fun onUserDataListLoaded(dataList: List<User>) {
    }

    override fun onUserActionSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onUserActionError(error: String) {
        Toast.makeText(this, "Lỗi: $error", Toast.LENGTH_LONG).show()
    }
}