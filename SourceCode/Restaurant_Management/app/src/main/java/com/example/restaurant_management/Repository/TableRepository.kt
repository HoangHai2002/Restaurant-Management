package com.example.restaurant_management.Repository

import com.example.restaurant_management.Models.Table
import com.google.android.material.tabs.TabLayout.Tab
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TableRepository : BaseRepository<Table> {

    private val userRef = FirebaseDatabase.getInstance().getReference("Table")

    override fun getById(id: String, callback: BaseRepository.DataCallback<Table>) {
        userRef.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children){
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
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var listTable = mutableListOf<Table>()
                for (it in snapshot.children){
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
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children){
                    val table = it.getValue(Table::class.java)
                    table?.let {
                        if (table.tableName == data.tableName){
                            return callback.onFailure("Tên bàn đã tồn tại")
                        }
                    }
                }
                data.id = userRef.push().key
                userRef.child(data.id!!).setValue(data).addOnSuccessListener {
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
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children){
                     val table = it.getValue(Table::class.java)
                    table?.let {
                        if(table.id != id){
                            if (table.tableName == data.tableName){
                                callback.onFailure("Tên bàn đã tồn tại")
                                return
                            }
                        }
                    }
                }
                userRef.child(id).setValue(data).addOnSuccessListener {
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
        userRef.child(id).removeValue().addOnSuccessListener {
            callback.onSuccess()
        }.addOnFailureListener {
            callback.onFailure("Xóa không thành công")
        }
    }
}