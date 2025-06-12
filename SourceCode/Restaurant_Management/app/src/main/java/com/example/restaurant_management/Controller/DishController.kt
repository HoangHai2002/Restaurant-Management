package com.example.restaurant_management.Controller

import com.example.restaurant_management.Models.Dish
import com.example.restaurant_management.Repository.BaseRepository
import com.example.restaurant_management.Repository.DishRepository


interface IDishView {
    fun onDishDataLoaded(data: Dish?)
    fun onDishDataListLoaded(dataList: List<Dish>)
    fun onDishActionSuccess(message: String)
    fun onDishActionError(error: String)
}
class DishController(val view : IDishView) {

    private var repository = DishRepository()

    fun getById(id : String){
        repository.getById(id, object : BaseRepository.DataCallback<Dish>{
            override fun onSuccess(data: Dish?) {
                view.onDishDataLoaded(data)
            }

            override fun onFailure(error: String) {
                view.onDishActionError(error)
            }
        })
    }

    fun getAll(){
        repository.getAll(object : BaseRepository.ListCallback<Dish>{
            override fun onSuccess(dataList: List<Dish>) {
                view.onDishDataListLoaded(dataList)
            }

            override fun onFailure(error: String) {
                view.onDishActionError(error)
            }
        })
    }

    fun add(data: Dish){
        repository.add(data, object : BaseRepository.ActionCallback{
            override fun onSuccess() {
                view.onDishActionSuccess("Thêm thành công")
            }

            override fun onFailure(error: String) {
                view.onDishActionError(error)
            }
        })
    }

    fun update(id: String, data: Dish){
        repository.update(id, data, object : BaseRepository.ActionCallback{
            override fun onSuccess() {
                view.onDishActionSuccess("Cập nhật thành công")
            }

            override fun onFailure(error: String) {
                view.onDishActionError(error)
            }
        })
    }

    fun delete(id: String){
        repository.delete(id, object : BaseRepository.ActionCallback{
            override fun onSuccess() {
                view.onDishActionSuccess("Xóa thành công")
            }

            override fun onFailure(error: String) {
                view.onDishActionError(error)
            }
        })
    }
}