package com.example.bkfoodcourt.ui.cook

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.adapter.CustomListviewAdapter
import com.example.bkfoodcourt.adapter.OrderListviewAdapter
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.model.CookModel
import com.example.bkfoodcourt.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_see_order_list.*
import kotlinx.android.synthetic.main.activity_show_employee_register_request.*
import java.io.Serializable

class SeeOrderListActivity : AppCompatActivity() {
    var orderList = ArrayList<Order>()
    val orderRef = Common.myRef.child("Order")
    lateinit var adapter : OrderListviewAdapter
    var select : Order? = null
    var selectPosition : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_order_list)

        adapter = OrderListviewAdapter(this, R.layout.order_listview_item, orderList)
        listView_order.adapter = adapter

        orderRef.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                orderList.clear()
                orderRef.removeEventListener(this)
                orderRef.addChildEventListener(this)
                adapter.notifyDataSetChanged()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var cur_order = p0.getValue(Order::class.java)!!
                cur_order.orderNumber = p0.key
                orderList.add(0, cur_order)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                orderList.clear()
                orderRef.removeEventListener(this)
                orderRef.addChildEventListener(this)
                adapter.notifyDataSetChanged()
            }
        })

        listView_order.setOnItemClickListener { adapter, view, i, l ->
            select = orderList[i]
            selectPosition = i
            Log.d("---------SELECT----------", select.toString())
            val intent = Intent(this, SeeOrderDetailActivity::class.java)
            Common.cur_order = select
            Log.d("---------common----------", Common.cur_order.toString())
            startActivity(intent)
        }
    }
}