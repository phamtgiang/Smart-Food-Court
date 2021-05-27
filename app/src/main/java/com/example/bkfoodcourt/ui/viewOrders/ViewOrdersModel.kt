package com.example.bkfoodcourt.ui.viewOrders
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bkfoodcourt.model.Order

class ViewOrdersModel : ViewModel(){
    var mutableLiveDateOrderList:MutableLiveData<List<Order>>
    init {
        mutableLiveDateOrderList = MutableLiveData()
    }
    fun setMutableLiveDataOrderList(orderList : List<Order>)
    {
        mutableLiveDateOrderList.value = orderList
    }
}