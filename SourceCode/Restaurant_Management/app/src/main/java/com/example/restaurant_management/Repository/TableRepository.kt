package com.example.restaurant_management.Repository

import android.util.Log
import android.widget.Toast
import com.example.restaurant_management.Models.Table
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TableRepository : BaseRepository<Table> {

    private val ref = FirebaseDatabase.getInstance().getReference("Table")

    override fun getById(id: String, callback: BaseRepository.DataCallback<Table>) {
        ref.orderByChild("id").equalTo(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (it in snapshot.children) {
                        val table = it.getValue(Table::class.java)
                        table?.let {
                            callback.onSuccess(table)
                            return
                        }
                    }
                    callback.onFailure("Không tìm thấy bàn")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.onFailure(error.message)
                }
            })
    }

    override fun getAll(callback: BaseRepository.ListCallback<Table>) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var listTable = mutableListOf<Table>()
                for (it in snapshot.children) {
                    val table = it.getValue(Table::class.java)
                    table?.let {
                        listTable.add(table)
                    }
                }
                callback.onSuccess(listTable)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun add(data: Table, callback: BaseRepository.ActionCallback) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children) {
                    val table = it.getValue(Table::class.java)
                    table?.let {
                        if (table.tableName == data.tableName) {
                            return callback.onFailure("Tên bàn đã tồn tại")
                        }
                    }
                }
                data.id = ref.push().key
                ref.child(data.id!!).setValue(data).addOnSuccessListener {
                    callback.onSuccess()
                }.addOnFailureListener {
                    callback.onFailure("Thêm bàn thất bại")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun update(id: String, data: Table, callback: BaseRepository.ActionCallback) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children) {
                    val table = it.getValue(Table::class.java)
                    table?.let {
                        if (table.id != id) {
                            if (table.tableName == data.tableName) {
                                callback.onFailure("Tên bàn đã tồn tại")
                                return
                            }
                        }
                    }
                }
                ref.child(id).setValue(data).addOnSuccessListener {
                    callback.onSuccess()
                }.addOnFailureListener {
                    callback.onFailure("Chỉnh sửa không thành công")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun delete(id: String, callback: BaseRepository.ActionCallback) {
        ref.child(id).removeValue().addOnSuccessListener {
            callback.onSuccess()
        }.addOnFailureListener {
            callback.onFailure("Xóa không thành công")
        }
    }

    fun getByTableAreaAndStatus(tableAreaId: String, status: String, callback: BaseRepository.ListCallback<Table>) {
        if (tableAreaId == ""){
            if (status == "Trống"){
                ref.orderByChild("status").equalTo(status).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val listTable = mutableListOf<Table>()
                        for (it in snapshot.children) {
                            val table = it.getValue(Table::class.java)
                            table?.let {
                                listTable.add(table)
                            }
                        }
                        callback.onSuccess(listTable)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback.onFailure(error.message)
                    }
                })
            }else{
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val listTable = mutableListOf<Table>()
                        for (it in snapshot.children) {
                            val table = it.getValue(Table::class.java)
                            table?.let {
                                if (table.status != "Trống"){
                                    listTable.add(table)
                                }

                            }
                        }
                        callback.onSuccess(listTable)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback.onFailure(error.message)
                    }
                })
            }
        } else{
            if (status == ""){
                ref.orderByChild("tableAreaId").equalTo(tableAreaId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val listTable = mutableListOf<Table>()
                        for (it in snapshot.children) {
                            val table = it.getValue(Table::class.java)
                            table?.let {
                                listTable.add(table)
                            }
                        }
                        callback.onSuccess(listTable)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback.onFailure(error.message)
                    }
                })
            } else{
                ref.orderByChild("tableAreaId").equalTo(tableAreaId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val listTable = mutableListOf<Table>()
                        for (it in snapshot.children) {
                            val table = it.getValue(Table::class.java)
                            table?.let {
                                if (status == "Trống"){
                                    if (table.status == status){
                                        listTable.add(table)
                                    }

                                }
                                if(status == "Sử dụng"){
                                    if(table.status != "Trống"){
                                        listTable.add(table)
                                    }
                                }

                            }
                        }
                        callback.onSuccess(listTable)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback.onFailure(error.message)
                    }
                })
            }
        }
    }

    fun changeStatus(id: String, status: String, callback: BaseRepository.ActionCallback) {
        val isActiveUpdate = mapOf<String, Any>("status" to status)
        ref.child(id).updateChildren(isActiveUpdate).addOnSuccessListener {
            callback.onSuccess()
        }.addOnFailureListener {
            callback.onFailure("Thay đổi không thành công")
        }
    }

    fun getAllTableByStatusTrong(callback: BaseRepository.ListCallback<Table>) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var listTable = mutableListOf<Table>()
                for (it in snapshot.children) {
                    val table = it.getValue(Table::class.java)
                    table?.let {
                        if(it.status == "Trống"){
                            listTable.add(table)
                        }
                    }
                }
                callback.onSuccess(listTable)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }
}