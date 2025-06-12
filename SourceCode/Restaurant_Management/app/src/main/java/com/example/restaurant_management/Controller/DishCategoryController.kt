package com.example.restaurant_management.Controller

import com.example.restaurant_management.Models.Dish
import com.example.restaurant_management.Models.DishCategory
import com.example.restaurant_management.Repository.BaseRepository
import com.example.restaurant_management.Repository.DishCategoryRepository
import com.example.restaurant_management.Repository.DishRepository

interface IDishCategoryView {
    fun onDishCategoryDataLoaded(data: DishCategory?)
    fun onDishCategoryDataListLoaded(dataList: List<DishCategory>)
    fun onDishCategoryActionSuccess(message: String)
    fun onDishCategoryActionError(error: String)
}
class DishCategoryController(val view : IDishCategoryView) {

    private var repository = DishCategoryRepository()

    fun getById(id : String){
        repository.getById(id, object : BaseRepository.DataCallback<DishCategory>{
            override fun onSuccess(data: DishCategory?) {
                view.onDishCategoryDataLoaded(data)
            }

            override fun onFailure(error: String) {
                view.onDishCategoryActionError(error)
            }
        })
    }

    fun getAll(){
        repository.getAll(object : BaseRepository.ListCallback<DishCategory>{
            override fun onSuccess(dataList: List<DishCategory>) {
                view.onDishCategoryDataListLoaded(dataList)
            }

            override fun onFailure(error: String) {
                view.onDishCategoryActionError(error)
            }
        })
    }

    fun add(data: DishCategory){
        repository.add(data, object : BaseRepository.ActionCallback{
            override fun onSuccess() {
                view.onDishCategoryActionSuccess("Thêm thành công")
            }

            override fun onFailure(error: String) {
                view.onDishCategoryActionError(error)
            }
        })
    }

    fun update(id: String, data: DishCategory){
        repository.update(id, data, object : BaseRepository.ActionCallback{
            override fun onSuccess() {
                view.onDishCategoryActionSuccess("Cập nhật thành công")
            }

            override fun onFailure(error: String) {
                view.onDishCategoryActionError(error)
            }
        })
    }

    fun delete(id: String){
        repository.delete(id, object : BaseRepository.ActionCallback{
            override fun onSuccess() {
                view.onDishCategoryActionSuccess("Xóa thành công")
            }

            override fun onFailure(error: String) {
                view.onDishCategoryActionError(error)
            }
        })
    }
}