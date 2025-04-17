package com.example.restaurant_management.View

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant_management.Adapter.IClickUserItem
import com.example.restaurant_management.Adapter.UserManagementAdapter
import com.example.restaurant_management.Controller.IUserView
import com.example.restaurant_management.Controller.UserController
import com.example.restaurant_management.Models.User
import com.example.restaurant_management.databinding.ActivityUserManagementBinding

class UserManagementActivity : AppCompatActivity(),IUserView {
    private lateinit var bind: ActivityUserManagementBinding
    private lateinit var listUser: MutableList<User>
    private lateinit var adapter: UserManagementAdapter
    private lateinit var controller: UserController
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityUserManagementBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setSupportActionBar(bind.toolbar1)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.toolbar1.setTitle("Quản lý nhân viên")

        // Event click option item User
        val listener = object : IClickUserItem{
            override fun onUpdate(user: User, position: Int) {
                val intent = Intent(this@UserManagementActivity, EditUserActivity::class.java)
                intent.putExtra("userId", user.id)
                startActivity(intent)
            }

            override fun onDelete(user: User, position: Int) {
                AlertDialog.Builder(this@UserManagementActivity)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa người dùng này?")
                    .setPositiveButton("Xóa"){dialog, which -> controller.delete(user.id!!)}
                    .setNegativeButton("Hủy"){dialog, which -> dialog.dismiss()}
                    .show()
            }


            @SuppressLint("SuspiciousIndentation")
            override fun onChangeIsActive(user: User, position: Int) {
                val builder = AlertDialog.Builder(this@UserManagementActivity)
                    builder.setTitle("Xác nhận")
                if (user.active){
                    builder.setMessage("Bạn có chắc chắn muốn vô hiệu hóa tài khoản người dùng này?\n")
                }else{
                    builder.setMessage("Bạn có chắc chắn muốn kích hoạt tài khoản người dùng này?\n")
                }
                builder.setPositiveButton("Thay đổi"){dialog, which ->
                    Log.d("Check Active", user.active.toString())

                    controller.changeActive(user.id!!,user.active)
                    dialog.dismiss()
                }
                builder.setNegativeButton("Hủy"){dialog, which ->
                    dialog.dismiss()
                }
                builder.show()
            }
        }

        // Setup Rcv
        listUser = mutableListOf()
        adapter = UserManagementAdapter(listUser, listener)
        bind.rcListUser.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.rcListUser.adapter = adapter


        // Call Data
        controller = UserController(this)
        controller.getAll()

        bind.imvAddUser.setOnClickListener {
            val intent = Intent(this@UserManagementActivity, AddUserActivity::class.java)
            startActivity(intent)
        }

        bind.edtSearch.addTextChangedListener (object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                var keyword = p0.toString().trim().lowercase()
                val sortedList = listUser.sortedWith(compareByDescending<User>{
                    it.fullName?.lowercase()?.startsWith(keyword) == true
                }.thenByDescending {
                    it.fullName?.lowercase()?.contains(keyword) == true
                })

                listUser.clear()
                listUser.addAll(sortedList)
                adapter.notifyDataSetChanged()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        listUser.clear()
        controller.getAll()
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
        TODO("Not yet implemented")
    }

    override fun onUserDataListLoaded(dataList: List<User>) {
        listUser.clear()
        listUser.addAll(dataList)
        adapter.notifyDataSetChanged()
    }

    override fun onUserActionSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        controller.getAll()
    }

    override fun onUserActionError(error: String) {
        Toast.makeText(this, "Lỗi: $error", Toast.LENGTH_LONG).show()
    }
}