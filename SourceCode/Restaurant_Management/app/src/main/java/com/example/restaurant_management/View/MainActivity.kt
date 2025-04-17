package com.example.restaurant_management.View

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurant_management.Controller.IUserView
import com.example.restaurant_management.Controller.UserController
import com.example.restaurant_management.Models.User
import com.example.restaurant_management.R
import com.example.restaurant_management.Repository.Preferences
import com.example.restaurant_management.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), IUserView {
    lateinit var bind: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // Setup Navigation_View
        setSupportActionBar(bind.toolbar)
        supportActionBar?.title = ""
        toggle =ActionBarDrawerToggle(
            this,
            bind.drawerLayout,
            bind.toolbar,
            R.string.open,
            R.string.close
        )
        bind.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Event Navigation_View
        val navigationView: NavigationView =bind.navView
        val menu: Menu =navigationView.menu
        val qlKhuVuc:MenuItem = menu.findItem(R.id.qlKhuVucBan)
        val qlBan:MenuItem = menu.findItem(R.id.qlBan)
        val qlUser:MenuItem = menu.findItem(R.id.qlUser)
        val profile:MenuItem = menu.findItem(R.id.profile)
        val qlMonAn:MenuItem = menu.findItem(R.id.qlMonAn)
        val qlLoaiMonAn:MenuItem = menu.findItem(R.id.qlLoaiMonAn)
        val qlDoanhThu:MenuItem = menu.findItem(R.id.qlDoanhThu)

        bind.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.reload ->{
                    finish()
                    val intent = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(intent)
                }

                R.id.profile -> {
                    val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                    startActivity(intent)
                }
                R.id.qlUser ->{
                    if (Preferences(this).isAdmin()) {
                        val intent = Intent(this@MainActivity, UserManagementActivity::class.java)
                        startActivity(intent)
                    } else{
                        Toast.makeText(this, "Cần quyền Admin để truy cập chức năng này", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.logout ->{
                    Preferences(this).logout()
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.qlKhuVucBan -> {
                    val intent = Intent(this@MainActivity, TableAreaManagementActivity::class.java)
                    startActivity(intent)
                }
                R.id.qlBan ->{
                    val intent = Intent(this@MainActivity, TableManagementActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        // Event transition bottom line
        bind.layoutTatca.setOnClickListener {
            bind.line1.visibility = View.VISIBLE
            bind.line2.visibility = View.GONE
            bind.line3.visibility = View.GONE
        }
        bind.layoutSudung.setOnClickListener {
            bind.line1.visibility = View.GONE
            bind.line2.visibility = View.VISIBLE
            bind.line3.visibility = View.GONE
        }
        bind.layoutControng.setOnClickListener {
            bind.line1.visibility = View.GONE
            bind.line2.visibility = View.GONE
            bind.line3.visibility = View.VISIBLE
        }

       // Set HeaderView
        val controller = UserController(this)
        val id = Preferences(this).getId()
        controller.getById(id!!)


    }

    override fun onUserDataLoaded(data: User?) {
        val headerView = bind.navView.getHeaderView(0)
        val tvFullname = headerView.findViewById<TextView>(R.id.tv_name)
        val tvRole = headerView.findViewById<TextView>(R.id.tv_role)


        tvFullname.text = data?.fullName
        tvRole.text =  data?.convertRoleToText(data.role!!)
    }

    override fun onUserDataListLoaded(dataList: List<User>) {
        TODO("Not yet implemented")
    }

    override fun onUserActionSuccess(message: String) {
        TODO("Not yet implemented")
    }

    override fun onUserActionError(error: String) {
        TODO("Not yet implemented")
    }
}