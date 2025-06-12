package com.example.restaurant_management.Repository

import com.example.restaurant_management.Models.DishCategory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DishCategoryRepository: BaseRepository<DishCategory> {

    private val userRef = FirebaseDatabase.getInstance().getReference("DishCategory")

    override fun getById(id: String, callback: BaseRepository.DataCallback<DishCategory>) {
        userRef.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children){
                    val dishCategory = it.getValue(DishCategory::class.java)
                    dishCategory?.let {
                        callback.onSuccess(dishCategory)
                        return
                    }
                }
                callback.onFailure("Không tìm thấy loại món ăn")
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun getAll(callback: BaseRepository.ListCallback<DishCategory>) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var listDishCategory = mutableListOf<DishCategory>()
                for (it in snapshot.children){
                    val dishCategory = it.getValue(DishCategory::class.java)
                    dishCategory?.let {
                        listDishCategory.add(dishCategory)
                    }
                }
                callback.onSuccess(listDishCategory)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun add(data: DishCategory, callback: BaseRepository.ActionCallback) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children){
                    val dishCategory = it.getValue(DishCategory::class.java)
                    dishCategory?.let {
                        if (dishCategory.dishCategoryName == data.dishCategoryName){
                            return callback.onFailure("Tên loại món ăn đã tồn tại")
                        }
                    }
                }
                data.id = userRef.push().key
                userRef.child(data.id!!).setValue(data).addOnSuccessListener {
                    callback.onSuccess()
                }.addOnFailureListener {
                    callback.onFailure("Thêm loại món ăn thất bại")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun update(id: String, data: DishCategory, callback: BaseRepository.ActionCallback) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children){
                    val dishCategory = it.getValue(DishCategory::class.java)
                    dishCategory?.let {
                        if(dishCategory.id != id){
                            if (dishCategory.dishCategoryName == data.dishCategoryName){
                                callback.onFailure("Tên loại món ăn đã tồn tại")
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