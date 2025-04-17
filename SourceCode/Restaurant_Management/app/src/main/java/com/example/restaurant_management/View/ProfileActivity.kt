package com.example.restaurant_management.View

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurant_management.Controller.IUserView
import com.example.restaurant_management.Controller.UserController
import com.example.restaurant_management.Models.User
import com.example.restaurant_management.Repository.Preferences
import com.example.restaurant_management.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity(), IUserView {
    lateinit var bind: ActivityProfileBinding
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

//    override fun onDataLoaded(data: User?) {
//        data?.let {
//            bind.tvFullname.setText(data.fullName)
//            bind.tvUsername.setText(data.userName)
//            bind.tvPassword.setText(data.passWord)
//            bind.tvRole.setText(data.convertRoleToText(data.role!!))
//        }
//
//    }
//
//    override fun onDataListLoaded(dataList: List<User>) {
//    }
//
//    override fun onActionSuccess(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onActionError(error: String) {
//        Toast.makeText(this, "Lỗi: $error", Toast.LENGTH_LONG).show()
//    }

    override fun onUserDataLoaded(data: User?) {
        data?.let {
            bind.tvFullname.setText(data.fullName)
            bind.tvUsername.setText(data.userName)
            bind.tvPassword.setText(data.passWord)
            bind.tvRole.setText(data.convertRoleToText(data.role!!))
        }
    }

    override fun onUserDataListLoaded(dataList: List<User>) {
        TODO("Not yet implemented")
    }

    override fun onUserActionSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onUserActionError(error: String) {
        Toast.makeText(this, "Lỗi: $error", Toast.LENGTH_LONG).show()
    }
}