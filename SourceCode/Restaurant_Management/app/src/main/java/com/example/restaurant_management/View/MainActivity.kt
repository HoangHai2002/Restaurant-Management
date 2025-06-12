package com.example.restaurant_management.View

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant_management.Adapter.IClickDishCategoryMainItem
import com.example.restaurant_management.Adapter.IClickTableMainItem
import com.example.restaurant_management.Adapter.TableAreaMainAdapter
import com.example.restaurant_management.Adapter.TableMainAdapter
import com.example.restaurant_management.Controller.ITableAreaView
import com.example.restaurant_management.Controller.ITableView
import com.example.restaurant_management.Controller.IUserView
import com.example.restaurant_management.Controller.TableAreaController
import com.example.restaurant_management.Controller.TableController
import com.example.restaurant_management.Controller.UserController
import com.example.restaurant_management.Models.Table
import com.example.restaurant_management.Models.TableArea
import com.example.restaurant_management.Models.User
import com.example.restaurant_management.R
import com.example.restaurant_management.Repository.Preferences
import com.example.restaurant_management.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), IUserView, ITableView, ITableAreaView {
    lateinit var bind: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var lishTableArea : MutableList<TableArea>
    lateinit var listTable : MutableList<Table>
    lateinit var tableAreaAdapter: TableAreaMainAdapter
    lateinit var tableAdapter: TableMainAdapter
    lateinit var tableAreaController : TableAreaController
    lateinit var tableController : TableController

    var tableAreaIdSelected = ""
    var tableAreaStatusSelected = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        tableAreaController = TableAreaController(this@MainActivity)
        tableController = TableController(this@MainActivity)
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

        // Click TableArea
        val tableAreaListener = object : IClickDishCategoryMainItem {
            override fun onClick(data: TableArea, pos: Int, selectedPos: Int) {
                if(selectedPos == RecyclerView.NO_POSITION){
                    tableAreaIdSelected =""
                    if (tableAreaStatusSelected == ""){
                        tableController.getAll()
                    }else{
                        // khu vuc rong va stt != tatca
                        tableController.getByTableAreaAndStatus(tableAreaIdSelected, tableAreaStatusSelected)
                    }
                } else{
                    tableAreaIdSelected = data.id!!
                    tableController.getByTableAreaAndStatus(tableAreaIdSelected, tableAreaStatusSelected)
                }
            }
        }

        // Set RC TableArea
        lishTableArea = mutableListOf()
        tableAreaAdapter = TableAreaMainAdapter(lishTableArea, tableAreaListener)
        bind.rcTableArea.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bind.rcTableArea.adapter = tableAreaAdapter
        tableAreaController.getAll()


        // Set RC Table
        val tableListener = object  : IClickTableMainItem{
            override fun onClick(data: Table) {
                if(data.status == "Trống"){
                    val intent = Intent(this@MainActivity, OrderActivity::class.java)
                    intent.putExtra("TableId", data.id)
                    startActivity(intent)
                } else{
                    val intent = Intent(this@MainActivity, TableActiveActivity::class.java)
                    intent.putExtra("Id", data.id)
                    startActivity(intent)
                }
            }
        }
        listTable = mutableListOf()
        tableAdapter = TableMainAdapter(listTable, tableListener)
        bind.rcTable.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        bind.rcTable.adapter = tableAdapter
        tableController.getAll()

        // Event Navigation_View
        val navigationView: NavigationView =bind.navView
        val menu: Menu =navigationView.menu
        val qlBan = menu.findItem(R.id.qlBan)
        val qlKhuVucBan = menu.findItem(R.id.qlKhuVucBan)
        val qlMonAn = menu.findItem(R.id.qlMonAn)
        val qlLoaiMonAn = menu.findItem(R.id.qlLoaiMonAn)
        val qlUser = menu.findItem(R.id.qlUser)
        val qlBep = menu.findItem(R.id.qlBep)
        val qlDoanhThu = menu.findItem(R.id.qlDoanhThu)
        if (!Preferences(this).isAdmin()){
            qlBan.isVisible = false
            qlKhuVucBan.isVisible = false
            qlMonAn.isVisible = false
            qlLoaiMonAn.isVisible = false
            qlUser.isVisible = false
        }
        if (Preferences(this).getRole() == "Thu Ngân"){
            qlBep.isVisible = false
        }
        if (Preferences(this).getRole() == "Bếp"){
            qlDoanhThu.isVisible = false
        }
        if (Preferences(this).getRole().toString() == "Phục vụ"){
            qlDoanhThu.isVisible = false
            qlBep.isVisible = false
        }
        Log.d("role",Preferences(this).getRole().toString() )
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
                R.id.qlMonAn ->{
                    val intent = Intent(this@MainActivity, DishManagementActivity::class.java)
                    startActivity(intent)
                }
                R.id.qlLoaiMonAn ->{
                    val intent = Intent(this@MainActivity, DishCategoryManagementActivity::class.java)
                    startActivity(intent)
                }
                R.id.qlBep ->{
                    val intent = Intent(this@MainActivity, KitchenActivity::class.java)
                    startActivity(intent)
                }
                R.id.qlDoanhThu ->{
                    val intent = Intent(this@MainActivity, RevenueActivity::class.java)
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
            tableAreaStatusSelected = ""
            if (tableAreaIdSelected == ""){
                tableController.getAll()
            }else{
                tableController.getByTableAreaAndStatus(tableAreaIdSelected, tableAreaStatusSelected)
            }
        }
        bind.layoutSudung.setOnClickListener {
            bind.line1.visibility = View.GONE
            bind.line2.visibility = View.VISIBLE
            bind.line3.visibility = View.GONE
            tableAreaStatusSelected = "Sử dụng"
            tableController.getByTableAreaAndStatus(tableAreaIdSelected, tableAreaStatusSelected)
        }
        bind.layoutControng.setOnClickListener {
            bind.line1.visibility = View.GONE
            bind.line2.visibility = View.GONE
            bind.line3.visibility = View.VISIBLE
            tableAreaStatusSelected = "Trống"
            tableController.getByTableAreaAndStatus(tableAreaIdSelected, tableAreaStatusSelected)
        }

       // Set HeaderView
        val controller = UserController(this)
        val id = Preferences(this).getId()
        controller.getById(id!!)


    }

    override fun onResume() {
        super.onResume()
        tableController.getAll()
    }

    // User
    override fun onUserDataLoaded(data: User?) {
        val headerView = bind.navView.getHeaderView(0)
        val tvFullname = headerView.findViewById<TextView>(R.id.tv_name)
        val tvRole = headerView.findViewById<TextView>(R.id.tv_role)


        tvFullname.text = data?.fullName
        tvRole.text =  data?.convertRoleToText(data.role!!)
    }

    override fun onUserDataListLoaded(dataList: List<User>) {
    }

    override fun onUserActionSuccess(message: String) {
    }

    override fun onUserActionError(error: String) {
    }

    // Table
    override fun onTableDataLoaded(data: Table?) {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTableDataListLoaded(dataList: List<Table>) {
        listTable.clear()
        val sortList = dataList.sortedByDescending { it.status}
        listTable.addAll(sortList)
        tableAdapter.notifyDataSetChanged()
    }

    override fun onTableActionSuccess(message: String) {
    }

    override fun onTableActionError(error: String) {
    }


    //TableArea
    override fun onTableAreaDataLoaded(data: TableArea?) {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTableAreaDataListLoaded(dataList: List<TableArea>) {
        lishTableArea.clear()
        lishTableArea.addAll(dataList)
        tableAreaAdapter.notifyDataSetChanged()
    }

    override fun onTableAreaActionSuccess(message: String) {

    }

    override fun onTableAreaActionError(error: String) {

    }
}