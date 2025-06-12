package com.example.restaurant_management.Repository

import android.util.Log
import com.example.restaurant_management.Models.Order
import com.example.restaurant_management.Models.OrderItem
import com.example.restaurant_management.Models.Table
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderRepository : BaseRepository<Order> {

    private val ref = FirebaseDatabase.getInstance().getReference("Order")

    override fun getById(id: String, callback: BaseRepository.DataCallback<Order>) {
        ref.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children) {
                    val order = it.getValue(Order::class.java)
                    order?.let {
                        callback.onSuccess(order)
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

    override fun getAll(callback: BaseRepository.ListCallback<Order>) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var listOrder = mutableListOf<Order>()
                for (it in snapshot.children) {
                    val order = it.getValue(Order::class.java)
                    order?.let {
                        listOrder.add(order)
                    }
                }
                callback.onSuccess(listOrder)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun add(data: Order, callback: BaseRepository.ActionCallback) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children) {
                    val order = it.getValue(Order::class.java)
                    order?.let {
                        if (order.tableId == data.tableId) {
                            return callback.onFailure("Bàn đang có đơn")
                        }
                    }
                }
                for (item in data.orderItem) {
                    val itemKey = ref.push().key
                    item.orderItemId = itemKey
                }
                data.id = ref.push().key
                ref.child(data.id!!).setValue(data).addOnSuccessListener {
                    callback.onSuccess()
                }.addOnFailureListener {
                    callback.onFailure("Thêm đơn thất bại")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun update(id: String, data: Order, callback: BaseRepository.ActionCallback) {

        ref.child(id).setValue(data).addOnSuccessListener {
            callback.onSuccess()
        }.addOnFailureListener {
            callback.onFailure("Chỉnh sửa không thành công")
        }


    }

    override fun delete(id: String, callback: BaseRepository.ActionCallback) {
        ref.child(id).removeValue().addOnSuccessListener {
            callback.onSuccess()
        }.addOnFailureListener {
            callback.onFailure("Xóa không thành công")
        }
    }

    fun getByTableId(tableId: String, callback: BaseRepository.DataCallback<Order>) {
        ref.orderByChild("tableId").equalTo(tableId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children) {
                    val order = it.getValue(Order::class.java)
                    order?.let {
                        if (order.status) {
                            callback.onSuccess(order)
                            return
                        }

                    }
                }
                callback.onFailure("Không tìm thấy món ăn")
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    fun updateAllOrderItem(
        id: String,
        orderItem: MutableList<OrderItem>,
        callback: BaseRepository.ActionCallback
    ) {
        var sum = 0.0
        for (it in orderItem) {
            if (it.orderItemId == "") {
                val orderItemKey = ref.push().key
                it.orderItemId = orderItemKey
            }
            sum += it.price * it.quantity
        }
        val isOrderItemUpdate = mapOf<String, Any>("orderItem" to orderItem, "total" to sum)
        ref.child(id).updateChildren(isOrderItemUpdate).addOnSuccessListener {
            callback.onSuccess()
        }.addOnFailureListener {
            callback.onFailure("Thay đổi không thành công")
        }
    }

    fun getOrderItemByStatus(status: String, callback: BaseRepository.ListCallback<OrderItem>) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var listOrderItem = mutableListOf<OrderItem>()
                for (it in snapshot.children) {
                    val order = it.getValue(Order::class.java)
                    order?.let {
                        for (it in order.orderItem) {
                            if (it.status == status) {
                                listOrderItem.add(it)
                            }
                        }
                    }
                }
                callback.onSuccess(listOrderItem)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    fun updateStatusOrderItem(
        orderItem: OrderItem,
        status: String,
        callback: BaseRepository.ActionCallback
    ) {
        ref.orderByChild("id").equalTo(orderItem.orderId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children){
                    val order = it.getValue(Order::class.java)
                    order?.let {
                       val index =  order.orderItem.indexOfFirst { it == orderItem }
                        if (index != -1){
                         order.orderItem[index].status = status
                            val isOrderItemUpdate = mapOf<String, Any>("orderItem" to order.orderItem)
                            ref.child(orderItem.orderId!!).updateChildren(isOrderItemUpdate).addOnSuccessListener {
                                callback.onSuccess()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }
    fun updateGuest(id: String, guest : Int, callback: BaseRepository.ActionCallback){
        val update = mapOf<String, Any>("guest" to guest)
       ref.child(id).updateChildren(update).addOnSuccessListener {
           callback.onSuccess()
       }.addOnFailureListener { callback.onFailure("Error") }
    }

    fun changeTable(table: Table,order: Order, callback: BaseRepository.ActionCallback){

        ref.orderByChild("id").equalTo(order.id).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children){
                    val order = it.getValue(Order ::class.java)
                    order?.let {
                        Log.d("1" , "OK")
                        order.tableId = table.id
                        order.tableName = table.tableName
                        for (item in order.orderItem){
                            item.tableName = table.tableName
                        }
                        order.id?.let { it1 ->
                            Log.d("2" , "OK")
                            ref.child(it1).setValue(order).addOnSuccessListener {
                                Log.d("3" , "OK")
                                callback.onSuccess()
                            }.addOnFailureListener {
                                callback.onFailure("Chuyển bàn không thành công")
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.toString())
            }
        })
    }
}
