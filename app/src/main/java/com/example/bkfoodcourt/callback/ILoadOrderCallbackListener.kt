package com.example.bkfoodcourt.callback

import com.example.bkfoodcourt.model.Order

interface ILoadOrderCallbackListener {
    fun onLoadOrderSuccess(orderList: List<Order>)
    fun onLoadOrderFailed(message:String)
}