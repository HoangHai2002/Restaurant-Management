package com.example.restaurant_management.Repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.restaurant_management.Models.Bill
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class BillRepository : BaseRepository<Bill> {
    private val ref = FirebaseDatabase.getInstance().getReference("Bill")

    override fun getById(id: String, callback: BaseRepository.DataCallback<Bill>) {
        ref.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (it in snapshot.children) {
                    val bill = it.getValue(Bill::class.java)
                    bill?.let {
                        callback.onSuccess(bill)
                        return
                    }
                }
                callback.onFailure("Error Get Bill!")
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun getAll(callback: BaseRepository.ListCallback<Bill>) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var listBill = mutableListOf<Bill>()
                for (it in snapshot.children) {
                    val bill = it.getValue(Bill::class.java)
                    bill?.let {
                        listBill.add(bill)
                    }
                }
                listBill.reverse()
                callback.onSuccess(listBill)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    override fun add(data: Bill, callback: BaseRepository.ActionCallback) {
        data.id = ref.push().key
        ref.child(data.id!!).setValue(data).addOnSuccessListener {
            callback.onSuccess()
        }.addOnFailureListener {
            callback.onFailure("Thanh toán thất bại")
        }
    }

    override fun update(id: String, data: Bill, callback: BaseRepository.ActionCallback) {
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
    fun getRevenueByWeek(week : List<LocalDate>, callback: BaseRepository.ListCallback<BarEntry>){
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val listCallback = mutableListOf<BarEntry>()
                var count = 0f
                for (date in week){
                    val formater = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    var sum = 0f
                    for (it in snapshot.children){
                        val bill = it.getValue(Bill :: class.java)
                        bill?.let {
                            val localDate = LocalDate.parse(bill.createdAt, formater)
                            if (localDate == date){
                                sum += bill.order?.total?.toFloat() ?: 0f
                            }
                        }
                    }
                    listCallback.add(BarEntry(count, sum))
                    count++
                }
                callback.onSuccess(listCallback)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    fun getRevenueByYear(year : List<YearMonth>, callback: BaseRepository.ListCallback<BarEntry>){
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val listCallback = mutableListOf<BarEntry>()
                var count = 0f
                for (month in year){
                    val formater = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    var sum = 0f
                    for (it in snapshot.children){
                        val bill = it.getValue(Bill :: class.java)
                        bill?.let {
                            val yearMonth = YearMonth.parse(bill.createdAt, formater)
                            if (yearMonth == month){
                                sum += bill.order?.total?.toFloat() ?: 0f
                            }
                        }
                    }
                    listCallback.add(BarEntry(count, sum))
                    count++
                }
                callback.onSuccess(listCallback)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }

    fun getByDate(date : LocalDate, callback: BaseRepository.ListCallback<Bill>) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                var listBill = mutableListOf<Bill>()
                for (it in snapshot.children) {
                    val bill = it.getValue(Bill::class.java)
                    bill?.let {
                        val dateCreated = LocalDateTime.parse(bill.createdAt, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                        if (dateCreated.toLocalDate() == date){
                            listBill.add(bill)
                        }
                    }
                }
                listBill.reverse()
                callback.onSuccess(listBill)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onFailure(error.message)
            }
        })
    }
}