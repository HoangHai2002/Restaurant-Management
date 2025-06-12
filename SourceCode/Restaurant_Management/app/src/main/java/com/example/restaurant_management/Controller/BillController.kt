package com.example.restaurant_management.Controller

import com.example.restaurant_management.Models.Bill
import com.example.restaurant_management.Models.DishCategory
import com.example.restaurant_management.Repository.BaseRepository
import com.example.restaurant_management.Repository.BillRepository
import com.example.restaurant_management.Repository.DishCategoryRepository
import com.github.mikephil.charting.data.BarEntry
import java.time.LocalDate
import java.time.YearMonth

interface IBillView {
    fun onBillDataLoaded(data: Bill?)
    fun onBillListLoaded(dataList: List<Bill>)
    fun onBarEntryListLoaded(dataList: List<BarEntry>)
    fun onBillActionSuccess(message: String)
    fun onBillActionError(error: String)
}
class BillController(var view : IBillView) {
    private var repository = BillRepository()

    fun getById(id : String){
        repository.getById(id, object : BaseRepository.DataCallback<Bill>{
            override fun onSuccess(data: Bill?) {
                view.onBillDataLoaded(data)
            }

            override fun onFailure(error: String) {
                view.onBillActionError(error)
            }
        })
    }

    fun getAll(){
        repository.getAll(object : BaseRepository.ListCallback<Bill>{
            override fun onSuccess(dataList: List<Bill>) {
                view.onBillListLoaded(dataList)
            }

            override fun onFailure(error: String) {
                view.onBillActionError(error)
            }
        })
    }

    fun add(data: Bill){
        repository.add(data, object : BaseRepository.ActionCallback{
            override fun onSuccess() {
                view.onBillActionSuccess("Thanh toán thành công")
            }

            override fun onFailure(error: String) {
                view.onBillActionError(error)
            }
        })
    }

    fun update(id: String, data: Bill){
        repository.update(id, data, object : BaseRepository.ActionCallback{
            override fun onSuccess() {
                view.onBillActionSuccess("Cập nhật thành công")
            }

            override fun onFailure(error: String) {
                view.onBillActionError(error)
            }
        })
    }

    fun delete(id: String){
        repository.delete(id, object : BaseRepository.ActionCallback{
            override fun onSuccess() {
                view.onBillActionSuccess("Xóa thành công")
            }

            override fun onFailure(error: String) {
                view.onBillActionError(error)
            }
        })
    }
    fun getRevenueByWeek(week : List<LocalDate>){
        repository.getRevenueByWeek(week,  object:BaseRepository.ListCallback<BarEntry>{
            override fun onSuccess(dataList: List<BarEntry>) {
                view.onBarEntryListLoaded(dataList)
            }

            override fun onFailure(error: String) {
                view.onBillActionError(error)
            }
        })
    }
    fun getRevenueByYear(yearMonth: List<YearMonth>){
        repository.getRevenueByYear(yearMonth, object :BaseRepository.ListCallback<BarEntry>{
            override fun onSuccess(dataList: List<BarEntry>) {
                view.onBarEntryListLoaded(dataList)
            }

            override fun onFailure(error: String) {
                view.onBillActionError(error)
            }

        })
    }
    fun getByDate(date : LocalDate){
        repository.getByDate(date, object : BaseRepository.ListCallback<Bill>{
            override fun onSuccess(dataList: List<Bill>) {
                view.onBillListLoaded(dataList)
            }

            override fun onFailure(error: String) {
                view.onBillActionError(error)
            }
        })
    }
}