package com.example.restaurant_management.Controller

import android.util.Log
import com.example.restaurant_management.Models.User
import com.example.restaurant_management.Repository.BaseRepository
import com.example.restaurant_management.Repository.UserRepository

interface IUserView {
    fun onUserDataLoaded(data: User?)
    fun onUserDataListLoaded(dataList: List<User>)
    fun onUserActionSuccess(message: String)
    fun onUserActionError(error: String)
}

class UserController(val view: IUserView)  {
    private val repository = UserRepository()

    fun getById(id: String) {
        repository.getById(id, object : BaseRepository.DataCallback<User> {
            override fun onSuccess(data: User?) {
                view.onUserDataLoaded(data)
            }

            override fun onFailure(error: String) {
                view.onUserActionError(error)
            }
        })
    }

    fun getAll() {
        repository.getAll(object : BaseRepository.ListCallback<User> {
            override fun onSuccess(dataList: List<User>) {
                view.onUserDataListLoaded(dataList)
            }

            override fun onFailure(error: String) {
                view.onUserActionError(error)
            }
        })
    }

    fun add(data: User) {
        repository.add(data, object : BaseRepository.ActionCallback {
            override fun onSuccess() {
                view.onUserActionSuccess("Thêm thành công")
            }

            override fun onFailure(error: String) {
                view.onUserActionError(error)
            }
        })
    }

    fun update(id: String, data: User) {
        repository.update(id, data, object : BaseRepository.ActionCallback {
            override fun onSuccess() {
                view.onUserActionSuccess("Cập nhật thành công")
            }

            override fun onFailure(error: String) {
                view.onUserActionError(error)
            }
        })
    }

    fun delete(id: String) {
        repository.delete(id, object : BaseRepository.ActionCallback {
            override fun onSuccess() {
                view.onUserActionSuccess("Xóa thành công")
            }

            override fun onFailure(error: String) {
                view.onUserActionError(error)
            }
        })
    }

    fun checkLogin(username: String, password: String) {
        repository.checkLogin(username, password, object : BaseRepository.DataCallback<User> {
            override fun onSuccess(data: User?) {
                view.onUserDataLoaded(data)
            }

            override fun onFailure(error: String) {
                view.onUserActionError(error)
            }
        })
    }

    fun changeActive(id: String, active: Boolean) {
        Log.d("Check Active Controller", active.toString())

        repository.changeActive(id, active, object : BaseRepository.ActionCallback {
            override fun onSuccess() {
                view.onUserActionSuccess("Thay đổi thành công")
            }

            override fun onFailure(error: String) {
                view.onUserActionError(error)
            }

        })
    }
}