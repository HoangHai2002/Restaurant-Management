package com.example.restaurant_management.Controller

import com.example.restaurant_management.Models.TableArea
import com.example.restaurant_management.Repository.BaseRepository
import com.example.restaurant_management.Repository.TableAreaRepository
interface ITableAreaView {
    fun onTableAreaDataLoaded(data: TableArea?)
    fun onTableAreaDataListLoaded(dataList: List<TableArea>)
    fun onTableAreaActionSuccess(message: String)
    fun onTableAreaActionError(error: String)
}
class TableAreaController(val view : ITableAreaView) {
    private val repository = TableAreaRepository()

    fun getById(id: String) {
        repository.getById(id, object : BaseRepository.DataCallback<TableArea> {
            override fun onSuccess(data: TableArea?) {
                view.onTableAreaDataLoaded(data)
            }

            override fun onFailure(error: String) {
                view.onTableAreaActionError(error)
            }
        })
    }

    fun getAll() {
        repository.getAll(object : BaseRepository.ListCallback<TableArea> {
            override fun onSuccess(dataList: List<TableArea>) {
                view.onTableAreaDataListLoaded(dataList)
            }

            override fun onFailure(error: String) {
                view.onTableAreaActionError(error)
            }
        })
    }

    fun add(data: TableArea) {
        repository.add(data, object : BaseRepository.ActionCallback {
            override fun onSuccess() {
                view.onTableAreaActionSuccess("Thêm thành công")
            }

            override fun onFailure(error: String) {
                view.onTableAreaActionError(error)
            }
        })
    }

    fun update(id: String, data: TableArea) {
        repository.update(id, data, object : BaseRepository.ActionCallback {
            override fun onSuccess() {
                view.onTableAreaActionSuccess("Cập nhật thành công")
            }

            override fun onFailure(error: String) {
                view.onTableAreaActionError(error)
            }
        })
    }

    fun delete(id: String) {
        repository.delete(id, object : BaseRepository.ActionCallback {
            override fun onSuccess() {
                view.onTableAreaActionSuccess("Xóa thành công")
            }

            override fun onFailure(error: String) {
                view.onTableAreaActionError(error)
            }
        })
    }
}