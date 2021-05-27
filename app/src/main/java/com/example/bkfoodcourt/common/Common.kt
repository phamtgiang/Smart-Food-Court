package com.example.bkfoodcourt.common

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.TextView
import com.example.bkfoodcourt.model.CategoryModel
import com.example.bkfoodcourt.model.FoodModel
import com.example.bkfoodcourt.model.UserModel
import com.example.bkfoodcourt.model.Order
import com.example.bkfoodcourt.model.OwnerModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.StringBuilder
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

object Common {
    fun setSpanString(welcome: String, name: String?, txtUser: TextView?) {
        val builder = SpannableStringBuilder()
        builder.append(welcome)
        val txtSpannable = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(boldSpan,0,name!!.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(txtSpannable)
        txtUser!!.setText(builder!!,TextView.BufferType.SPANNABLE)
    }


    fun formatPrice(price: Double):String {
        if (price != 0.toDouble()) {
            val df = DecimalFormat("###,###")
            df.roundingMode = RoundingMode.HALF_UP
            val finalPrice = StringBuilder(df.format(price)).toString()
            //return finalPrice.replace(".", ",")
            return finalPrice
        }
        else return "0,00"
    }

    fun createOrderNumber(): String {
        return StringBuilder()
            .append(System.currentTimeMillis())
            .append(Math.abs(Random().nextInt()))
            .toString()
    }

    fun getDateOfWeek(i: Int): String {
        when(i){
            2 -> return "Monday"
            3 -> return "Tuesday"
            4 -> return "Wednesday"
            5 -> return "Thursday"
            6 -> return "Friday"
            7 -> return "Saturday"
            1 -> return "Sunday"
            else -> return "Unknown"
        }
    }

    fun convertStatusToText(orderStatus: Int): Any {
        when(orderStatus){
            0-> return "Placed"
            1-> return "Processing"
            2-> return "Done"
            else -> return "Unknown"
        }
    }

    val ORDER_REF: String = "Order"
    var order : Order ?= null
    var foodSelected: FoodModel?=null
    var categorySelected: CategoryModel?=null
    val CATEGORY_REF: String = "Category"
    val FULL_WIDTH_CONLUMN: Int = 1
    val DEFAULT_COLUMN_COUNT: Int = 0
    val POPULAR_REF: String="MostPopular"
    val BEST_DEALS_REF: String="BestDeals"
    val USER_REFERENCE : String ="Users"
    val MANAGER_REFERENCE : String = "Manager"
    val COOK_REFERENCE : String = "Cooks"
    val OWNER_REFERENCE : String = "Owners"
    val STAFF_REFERENCE : String = "Staffs"
    val IS_ACTIVE_REFERENCE : String = "active"
    var currentUser:UserModel?=UserModel()
    var currentOwner:OwnerModel?=OwnerModel()
    val COMMENT_REF:String = "Comments"
    val database = Firebase.database
    val myRef = database.getReference()
    val min_password_length = 6
    var cur_order : Order? = null
}