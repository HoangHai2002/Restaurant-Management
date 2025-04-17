package com.example.restaurant_management.Controller

import com.example.restaurant_management.Models.Table
import com.example.restaurant_management.Models.User
import com.example.restaurant_management.Repository.BaseRepository
import com.example.restaurant_management.Repository.TableRepository

interface ITableView {
    fun onTableDataLoaded(data: Table?)
    fun onTableDataListLoaded(dataList: List<Table>)
    fun onTableActionSuccess(message: String)
    fun onTableActionError(error: String)
}

class TableController(val view: ITableView) {
    private val repository = TableRepository()

    fun getById(id : String){
        repository.getById(id, object : BaseRepository.DataCallback<Table>{
            override fun onSuccess(data: Table?) {
                view.onTableDataLoaded(data)
            }

            override fun onFailure(error: String) {
                view.onTableActionError(error)
            }
        })
    }

    fun getAll(){
        repository.getAll(object : BaseRepository.ListCallback<Table>{
            override fun onSuccess(dataList: List<Table>) {
                view.onTableDataListLoaded(dataList)
            }

            override fun onFailure(error: String) {
                view.onTableActionError(error)
            }
        })
    }

    fun add(data: Table){
        repository.add(data, object : BaseRepository.ActionCallback{
            override fun onSuccess() {
                view.onTableActionSuccess("Thêm thành công")
            }

            override fun onFailure(error: String) {
                view.onTableActionError(error)
            }
        })
    }

    fun update(id: String, data: Table){
        repository.update(id, data, object : BaseRepository.ActionCallback{
            override fun onSuccess() {
                view.onTableActionSuccess("Cập nhật thành công")
            }

            override fun onFailure(error: String) {
                view.onTableActionError(error)
            }
        })
    }

    fun delete(id: String){
        repository.delete(id, object : BaseRepository.ActionCallback{
            override fun onSuccess() {
                view.onTableActionSuccess("Xóa thành công")
            }

            override fun onFailure(error: String) {
                view.onTableActionError(error)
            }
        })
    }
}