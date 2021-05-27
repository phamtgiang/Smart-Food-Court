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

class CustomListviewAdapter (var ctx : Context, var resource : Int, var Item : ArrayList<CookModel>) : ArrayAdapter<CookModel>(ctx, resource, Item) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)
        val view = layoutInflater.inflate(resource, null)

        val employeeName = view.findViewById<TextView>(R.id.EmployeeNameRequest)
        val employeeEmail = view.findViewById<TextView>(R.id.EmployeeEmailRequest)
        val employeePosition = view.findViewById<TextView>(R.id.EmployeePositionRequest)
        val employeePhone = view.findViewById<TextView>(R.id.EmployeePhoneRequest)
        val img : ImageView = view.findViewById<ImageView>(R.id.imageViewRequest)

        // set item data
        employeeName.text = "Name: " + Item[position].name
        employeeEmail.text = "Email: " + Item[position].email
        employeePhone.text = "Phone: " + Item[position].phone
        employeePosition.text = "Position: " + Item[position].type
        img.setImageResource(R.drawable.icon_name_tag)

        return view
    }
}