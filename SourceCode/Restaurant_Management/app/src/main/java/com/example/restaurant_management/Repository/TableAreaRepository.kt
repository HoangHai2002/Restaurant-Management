package com.example.restaurant_management.Repository

import com.example.restaurant_management.Models.TableArea
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TableAreaRepository : BaseRepository<TableArea> {

    private val userRef = FirebaseDatabase.getInstance().getReference("TableArea")

    override fun getById(id: String, callback: BaseRepository.DataCallback<TableArea>) {
        userRef.orderByChild("id").equalTo(id)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (it in snapshot.children){
                        val tableArea = it.getValue(TableArea::class.java)
                        callback.onSuccess(tableArea)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.onFailure(error.message)
                }
            })
    }

    override fun getAll(callback: BaseRepository.ListCallback<TableArea>) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var listTableArea = mutableListOf<TableArea>()
                for (it in snapshot.children){
                    val tableArea = it.getValue(TableArea::class.java)
                    tableArea?.let {   listTableArea.add(tableArea) }
                }
                callback.onSuccess(listTableArea)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun add(data: TableArea, callback: BaseRepository.ActionCallback) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children){
                    val tableArea = it.getValue(TableArea::class.java)
                    tableArea?.let {
                        if (tableArea?.tableAreaName == data.tableAreaName){
                            callback.onFailure("Tên khu vực đã tồn tại")
                            return
                        }
                    }

                }
                data.id = userRef.push().key
                userRef.child(data.id!!).setValue(data).addOnSuccessListener {
                    callback.onSuccess()
                }.addOnFailureListener {
                    callback.onFailure("Thêm khu vực thất bại")
                }
            }

            override fun onCancelled(error: DatabaseError) {
               callback.onFailure(error.message)
            }
        })
    }

    override fun update(id: String, data: TableArea, callback: BaseRepository.ActionCallback) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children){
                    val tableArea = it.getValue(TableArea::class.java)
                    tableArea?.let {
                        if (tableArea.id != data.id){
                            if (tableArea.tableAreaName == data.tableAreaName){
                                callback.onFailure("Tên khu vực đã tồn tại")
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