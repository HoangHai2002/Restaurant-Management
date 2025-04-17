package com.example.restaurant_management.Repository

interface BaseRepository<T> {
    interface DataCallback<T> {
        fun onSuccess(data: T?)
        fun onFailure(error: String)
    }

    interface ListCallback<T> {
        fun onSuccess(dataList: List<T>)
        fun onFailure(error: String)
    }

    interface ActionCallback {
        fun onSuccess()
        fun onFailure(error: String)
    }

    fun getById(id: String, callback: DataCallback<T>)
    fun getAll(callback: ListCallback<T>)
    fun add(data: T, callback: ActionCallback)
    fun update(id: String, data: T, callback: ActionCallback)
    fun delete(id: String, callback: ActionCallback)
}