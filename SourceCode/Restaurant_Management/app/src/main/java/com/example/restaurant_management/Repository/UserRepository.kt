package com.example.restaurant_management.Repository

import android.util.Log
import com.example.restaurant_management.Models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepository : BaseRepository<User> {
    private val userRef = FirebaseDatabase.getInstance().getReference("User")

    override fun getById(id: String, callback: BaseRepository.DataCallback<User>) {
        userRef.orderByChild("id").equalTo(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (it in snapshot.children) {
                        val user = it.getValue(User::class.java)
                        user?.let {
                            callback.onSuccess(user)
                            return
                        }
                    }
                    callback.onFailure("Không tìm thấy người dùng")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.onFailure(error.message)
                }
            })
    }

    override fun getAll(callback: BaseRepository.ListCallback<User>) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var listUser = mutableListOf<User>()
                for (it in snapshot.children) {
                    val user = it.getValue(User::class.java)

                    user?.let {
                        listUser.add(it)
                    }
                }
                listUser.sortByDescending { it.active }
                callback.onSuccess(listUser)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun add(data: User, callback: BaseRepository.ActionCallback) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children) {
                    val user = it.getValue(User::class.java)
                    if (user?.fullName == data.fullName) {
                        callback.onFailure("Tên người dùng đã tồn tại")
                        return
                    }
                    if (user?.userName == data.userName) {
                        callback.onFailure("Tên đăng nhập đã tồn tại")
                        return
                    }
                }
                data.id = userRef.push().key
                userRef.child(data.id!!).setValue(data).addOnSuccessListener {
                    callback.onSuccess()
                }.addOnFailureListener {
                    callback.onFailure("Thêm người dùng thất bại")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun update(id: String, data: User, callback: BaseRepository.ActionCallback) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children) {
                    val user = it.getValue(User::class.java)
                    user?.let {
                        if (user.id != id) {
                            if (user.fullName == data.fullName) {
                                callback.onFailure("Tên người dùng đã tồn tại")
                                return
                            }
                            if (user.userName == data.userName) {
                                callback.onFailure("Tên đăng nhập đã tồn tại")
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

    fun checkLogin(
        username: String,
        passwrod: String,
        callback: BaseRepository.DataCallback<User>
    ) {
        userRef.orderByChild("userName").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            val user = snap.getValue(User::class.java)
                            user?.let {
                                if (user.passWord == passwrod) {
                                    if (user.active == true){
                                        callback.onSuccess(user)
                                        return
                                    } else{
                                        callback.onFailure("Tài khoản của bạn đã bị vô hiệu hóa")
                                        return
                                    }

                                }
                            }
                        }
                        callback.onFailure("Sai tên đăng nhập hoặc mật khẩu")
                    } else {
                        callback.onFailure("Sai tên đăng nhập hoặc mật khẩu")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.onFailure("Lỗi" + error.message)
                }
            })
    }

    fun changeActive(id: String, isActive :Boolean, callback: BaseRepository.ActionCallback){
        val isActiveUpdate = mapOf<String, Any>("active" to !isActive)
        userRef.child(id).updateChildren(isActiveUpdate).addOnSuccessListener {
            callback.onSuccess()
        }.addOnFailureListener {
            callback.onFailure("Thay đổi không thành công")
        }
    }
}