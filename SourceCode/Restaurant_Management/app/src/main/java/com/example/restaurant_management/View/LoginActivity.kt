package com.example.restaurant_management.View

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurant_management.Controller.IUserView
import com.example.restaurant_management.Controller.UserController
import com.example.restaurant_management.Models.User
import com.example.restaurant_management.Repository.Preferences
import com.example.restaurant_management.databinding.ActivityLoginBinding
import com.google.firebase.database.DatabaseReference

class LoginActivity : AppCompatActivity(), IUserView {
    lateinit var bind: ActivityLoginBinding
    lateinit var userRef: DatabaseReference
    lateinit var preferences: Preferences
    private lateinit var controller: UserController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)

        preferences = Preferences(this)
        val intent = Intent(this@LoginActivity, MainActivity::class.java)

        if (preferences.isLogin()) {
            startActivity(intent)
            finish()
        }


        //new
        controller = UserController(this)

        val btn_login = bind.btnLogin
        btn_login.setOnClickListener {
            val userName = bind.edtUsername.text.toString().trim()
            val passWord = bind.edtPassword.text.toString().trim()
            if (userName != "" && passWord != "") {

                //new
                controller.checkLogin(userName, passWord)


//                val repo = UserRepository()
//                repo.checkLogin(userName, passWord) { callback ->
//                    if (callback != null) {
//                        startActivity(intent)
//
//                        callback.id?.let {
//                            preferences.saveLogin(
//                                callback.id!!,
//                                callback.role == 0
//                            )
//                        }
//                        finish()
//                    } else {
//                        Toast.makeText(
//                            this@LoginActivity,
//                            "Wrong username or password. Please try again!",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
            } else if (userName == "") {
                bind.edtUsername.error = "Please enter username"
            } else {
                bind.edtPassword.error = "Please enter password"
            }
        }
    }

//    override fun onDataLoaded(data: User?) {
//        data?.let {
//            val intent = Intent(this@LoginActivity, MainActivity::class.java)
//            startActivity(intent)
//            if (data.id != null) {
//                preferences.saveLogin(data.id!!, data.role == 0)
//            }
//        }
//    }
//
//    override fun onDataListLoaded(dataList: List<User>) {
//    }
//
//    override fun onActionSuccess(message: String) {
//
//    }
//
//    override fun onActionError(error: String) {
//        Toast.makeText(this, "Lỗi: $error", Toast.LENGTH_LONG).show()
//    }

    override fun onUserDataLoaded(data: User?) {
        data?.let {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            if (data.id != null) {
                preferences.saveLogin(data.id!!, data.role == 0)
            }
        }
    }

    override fun onUserDataListLoaded(dataList: List<User>) {
        TODO("Not yet implemented")
    }

    override fun onUserActionSuccess(message: String) {
        TODO("Not yet implemented")
    }

    override fun onUserActionError(error: String) {
        Toast.makeText(this, "Lỗi: $error", Toast.LENGTH_LONG).show()
    }
}