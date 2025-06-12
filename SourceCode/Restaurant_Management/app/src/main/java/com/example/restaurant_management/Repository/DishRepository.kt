package com.example.restaurant_management.Repository

import com.example.restaurant_management.Models.Dish
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DishRepository : BaseRepository<Dish> {

    private val userRef = FirebaseDatabase.getInstance().getReference("Dish")

    override fun getById(id: String, callback: BaseRepository.DataCallback<Dish>) {
        userRef.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children){
                    val dish = it.getValue(Dish::class.java)
                    dish?.let {
                        callback.onSuccess(dish)
                        return
                    }
                }
                callback.onFailure("Không tìm thấy món ăn")
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun getAll(callback: BaseRepository.ListCallback<Dish>) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var listDish = mutableListOf<Dish>()
                for (it in snapshot.children){
                    val dish = it.getValue(Dish::class.java)
                    dish?.let {
                        listDish.add(dish)
                    }
                }
                callback.onSuccess(listDish)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun add(data: Dish, callback: BaseRepository.ActionCallback) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children){
                    val dish = it.getValue(Dish::class.java)
                    dish?.let {
                        if (dish.dishName == data.dishName){
                            return callback.onFailure("Tên món ăn đã tồn tại")
                        }
                    }
                }
                data.id = userRef.push().key
                userRef.child(data.id!!).setValue(data).addOnSuccessListener {
                    callback.onSuccess()
                }.addOnFailureListener {
                    callback.onFailure("Thêm món ăn thất bại")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun update(id: String, data: Dish, callback: BaseRepository.ActionCallback) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children){
                    val dish = it.getValue(Dish::class.java)
                    dish?.let {
                        if(dish.id != id){
                            if (dish.dishName == data.dishName){
                                callback.onFailure("Tên món ăn đã tồn tại")
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