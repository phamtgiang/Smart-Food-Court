package com.example.bkfoodcourt.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cart" , primaryKeys = ["uid","foodId"])

class CartItem {
    @NonNull
    @ColumnInfo(name = "foodId")
    var foodId:String?=null

    @ColumnInfo(name = "foodName")
    var foodName:String?=null

    @ColumnInfo(name = "foodPrice")
    var foodPrice:Double=0.0

    @ColumnInfo(name = "foodQuantity")
    var foodQuantity:Int=0

    @NonNull
    @ColumnInfo(name = "uid")
    var uid:String?=null

    @ColumnInfo(name="userPhone")
    var userPhone:String?=null

    @ColumnInfo(name="foodImage")
    var foodImage:String?=null


    override fun equals(other: Any?): Boolean {
        if(other === this) return true
        if(other !is CartItem) return false
        val cartItem = other as CartItem?
        return cartItem!!.foodId == this.foodId
    }

    /** CAN ADD ADD ON FOR FOOD OR USER PHONE LATER **/
}