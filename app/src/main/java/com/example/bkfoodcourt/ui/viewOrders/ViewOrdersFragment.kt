package com.example.bkfoodcourt.ui.viewOrders

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.adapter.MyOrderAdapter
import com.example.bkfoodcourt.callback.ILoadOrderCallbackListener
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.model.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import java.util.*
import kotlin.collections.ArrayList

class ViewOrdersFragment : Fragment(), ILoadOrderCallbackListener {
    private var viewOrdersModel : ViewOrdersModel?=null

    internal lateinit var dialog : AlertDialog

    internal lateinit var recyclerOrder : RecyclerView

    internal  lateinit var listener : ILoadOrderCallbackListener

    override fun onLoadOrderSuccess(orderList: List<Order>) {
        dialog.dismiss()
        viewOrdersModel!!.setMutableLiveDataOrderList(orderList)
    }

    override fun onLoadOrderFailed(message: String) {
        dialog.dismiss()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewOrdersModel = ViewModelProviders.of(this).get(ViewOrdersModel::class.java!!)
        val root = inflater.inflate(R.layout.fragment_view_orders, container, false)
        initView(root)
        loadOrderFromFirebase()

        viewOrdersModel!!.mutableLiveDateOrderList.observe(this, Observer {
            Collections.reverse(it)
            val adapter = MyOrderAdapter(context!!, it!!)
            recyclerOrder!!.adapter = adapter

        })

        return root


    }

    private fun loadOrderFromFirebase() {
        dialog.show()
        val orderList = ArrayList<Order>()
        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
            .orderByChild("userId")
            .equalTo(Common.currentUser!!.uid!!)
            .limitToLast(100)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    listener.onLoadOrderFailed(p0.message!!)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (orderSnapShot in p0.children) {
                        val order = orderSnapShot.getValue(Order::class.java)
                        order!!.orderNumber = orderSnapShot.key
                        orderList.add(order!!)
                    }
                    listener.onLoadOrderSuccess(orderList)
                }

            })

    }

    private fun initView(root: View?) {
        listener = this
        dialog = SpotsDialog.Builder().setContext(context!!).setCancelable(false).build()
        recyclerOrder = root!!.findViewById(R.id.recycler_view_orders) as RecyclerView
        recyclerOrder.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context!!)
        recyclerOrder.layoutManager = layoutManager
        recyclerOrder.addItemDecoration(DividerItemDecoration(context!!, layoutManager.orientation))
    }
}