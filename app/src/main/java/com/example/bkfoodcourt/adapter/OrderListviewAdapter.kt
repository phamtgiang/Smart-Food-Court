package com.example.bkfoodcourt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.model.CookModel
import com.example.bkfoodcourt.model.Order
import com.example.bkfoodcourt.model.RechargeModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class OrderListviewAdapter (var ctx : Context, var resource : Int, var Item : ArrayList<Order>) : ArrayAdapter<Order>(ctx, resource, Item) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)
        val view = layoutInflater.inflate(resource, null)

        val date = view.findViewById<TextView>(R.id.textViewOrderDate)
        val numOfItem = view.findViewById<TextView>(R.id.textViewNumOfItem)
        val img : ImageView = view.findViewById<ImageView>(R.id.imageViewOrder)

        // set item data
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        date.text = "OrderID: " + Item[position].orderNumber.toString()
        numOfItem.text = "Num of item: " + Item[position].cartListItem?.size.toString()
        img.setImageResource(R.drawable.icon_order)

        return view
    }
}