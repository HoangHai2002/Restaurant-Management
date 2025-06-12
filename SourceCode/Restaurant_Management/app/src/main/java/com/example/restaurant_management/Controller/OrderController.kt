package com.example.restaurant_management.Controller

import android.util.Log
import com.example.restaurant_management.Models.Order
import com.example.restaurant_management.Models.OrderItem
import com.example.restaurant_management.Models.Table
import com.example.restaurant_management.Repository.BaseRepository
import com.example.restaurant_management.Repository.OrderRepository

interface IOrderView {
    fun onOrderDataLoaded(data: Order?)
    fun onOrderDataListLoaded(dataList: List<Order>)
    fun onOrderItemDataListLoaded(dataList: List<OrderItem>)
    fun onOrderActionSuccess(message: String)
    fun onOrderActionError(error: String)
}
class OrderController(var view : IOrderView )  {
    private val repository = OrderRepository()

    fun getById(id: String) {
        repository.getById(id, object : BaseRepository.DataCallback<Order> {
            override fun onSuccess(data: Order?) {
                view.onOrderDataLoaded(data)
            }

            override fun onFailure(error: String) {
                view.onOrderActionError(error)
            }
        })
    }

    fun getAll() {
        repository.getAll(object : BaseRepository.ListCallback<Order> {
            override fun onSuccess(dataList: List<Order>) {
                view.onOrderDataListLoaded(dataList)
            }

            override fun onFailure(error: String) {
                view.onOrderActionError(error)
            }
        })
    }

    fun add(data: Order) {
        repository.add(data, object : BaseRepository.ActionCallback {
            override fun onSuccess() {
                view.onOrderActionSuccess("Thêm thành công")
            }

            override fun onFailure(error: String) {
                view.onOrderActionError(error)
            }
        })
    }

    fun update(id: String, data: Order) {
        repository.update(id, data, object : BaseRepository.ActionCallback {
            override fun onSuccess() {
                view.onOrderActionSuccess("Cập nhật thành công")
            }

            override fun onFailure(error: String) {
                view.onOrderActionError(error)
            }
        })
    }

    fun delete(id: String) {
        repository.delete(id, object : BaseRepository.ActionCallback {
            override fun onSuccess() {
                view.onOrderActionSuccess("Xóa thành công")
            }

            override fun onFailure(error: String) {
                view.onOrderActionError(error)
            }
        })
    }
    fun getByTableId(tableId: String) {
        repository.getByTableId(tableId, object : BaseRepository.DataCallback<Order> {
            override fun onSuccess(data: Order?) {
                view.onOrderDataLoaded(data)
            }

            override fun onFailure(error: String) {
                view.onOrderActionError(error)
            }
        })
    }

    fun updateAllOrderItem(id: String, orderItem : MutableList<OrderItem>) {
        repository.updateAllOrderItem(id, orderItem,  object : BaseRepository.ActionCallback {
            override fun onSuccess() {
                view.onOrderActionSuccess("Success")
            }

            override fun onFailure(error: String) {
                view.onOrderActionError(error)
            }
        })
    }
    fun getOrderItemByStatus(status: String){
        repository.getOrderItemByStatus(status, object :BaseRepository.ListCallback<OrderItem>{
            override fun onSuccess(dataList: List<OrderItem>) {
                view.onOrderItemDataListLoaded(dataList)
            }

            override fun onFailure(error: String) {
              view.onOrderActionError(error)
            }
        })

    }
    fun updateStatusOrderItem(orderItem: OrderItem, status: String){
        repository.updateStatusOrderItem(orderItem, status, object :BaseRepository.ActionCallback{
            override fun onSuccess() {
                view.onOrderActionSuccess(status)
            }

            override fun onFailure(error: String) {
               view.onOrderActionError(error)
            }
        })
    }
    fun updateGuest(id: String, guest : Int){
        repository.updateGuest(id, guest, object : BaseRepository.ActionCallback{
            override fun onSuccess() {
                view.onOrderActionSuccess("Success")
            }

            override fun onFailure(error: String) {
                view.onOrderActionError(error)
            }
        })
    }

    fun changeTable(table: Table, order: Order) {
        repository.changeTable(table, order, object : BaseRepository.ActionCallback{
            override fun onSuccess() {
                Log.d("4" , "OK")
                view.onOrderActionSuccess("Chuyển bàn thành công")
            }

            override fun onFailure(error: String) {
                view.onOrderActionError(error)
            }
        })
    }

}