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
import com.example.bkfoodcourt.model.RechargeModel
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

class RechargeListviewAdapter (var ctx : Context, var resource : Int, var Item : ArrayList<RechargeModel>) : ArrayAdapter<RechargeModel>(ctx, resource, Item) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)
        val view = layoutInflater.inflate(resource, null)

        val date = view.findViewById<TextView>(R.id.textViewDate)
        val email = view.findViewById<TextView>(R.id.textViewEmail)
        val amount = view.findViewById<TextView>(R.id.textViewAmount)
        val img : ImageView = view.findViewById<ImageView>(R.id.imageViewRequest)

        // set item data
        date.text = "Date: " + Item[position].time?.format(DateTimeFormatter.ISO_DATE_TIME).toString()
        email.text = "Beneficiary: " + Item[position].email
        amount.text = "Amount: " + DecimalFormat("#,###").format(Item[position].amount.toInt()).toString() + " VND"
        img.setImageResource(R.drawable.icons_money_box)

        return view
    }
}