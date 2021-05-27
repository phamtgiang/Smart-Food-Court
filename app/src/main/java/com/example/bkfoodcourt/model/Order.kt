package com.example.bkfoodcourt.model

import com.example.bkfoodcourt.database.CartItem

class Order {
    var userId : String? = null
    var userName : String? =null
    var finalPayment : Double = 0.toDouble()
    var cartListItem : List<CartItem>? = null
    var createDate : Long = 0
    var orderNumber:String?=null
    var orderStatus : Int = 0
}