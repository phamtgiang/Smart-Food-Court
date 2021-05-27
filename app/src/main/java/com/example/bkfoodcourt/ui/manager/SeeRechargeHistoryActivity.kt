package com.example.bkfoodcourt.ui.manager

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.adapter.CustomListviewAdapter
import com.example.bkfoodcourt.adapter.RechargeListviewAdapter
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.model.CookModel
import com.example.bkfoodcourt.model.RechargeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_see_recharge_history.*
import kotlinx.android.synthetic.main.activity_show_employee_register_request.*

class SeeRechargeHistoryActivity : AppCompatActivity() {
    var recharge_hist = ArrayList<RechargeModel>()
    val rechargeRef = Common.myRef.child("RechargeHistory")
    lateinit var adapter : RechargeListviewAdapter
    var select : RechargeModel? = null
    var selectPosition : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_recharge_history)

        adapter = RechargeListviewAdapter(this, R.layout.recharge_history_listview_item, recharge_hist)
        listView_recharge_history.adapter = adapter

        rechargeRef.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                recharge_hist.clear()
                rechargeRef.removeEventListener(this)
                rechargeRef.addChildEventListener(this)
                adapter.notifyDataSetChanged()
                if (recharge_hist.size == 0){
                    Toast.makeText(this@SeeRechargeHistoryActivity, "Empty", Toast.LENGTH_LONG).show()
                }
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                recharge_hist.add(p0.getValue(RechargeModel::class.java)!!)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                recharge_hist.clear()
                rechargeRef.removeEventListener(this)
                rechargeRef.addChildEventListener(this)
                adapter.notifyDataSetChanged()
                if (recharge_hist.size == 0){
                    Toast.makeText(this@SeeRechargeHistoryActivity, "Empty", Toast.LENGTH_LONG).show()
                }
            }

        })
    }

//    override fun onCancelled(p0: DatabaseError) {
//    }
//
//    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//    }
//
//    override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//        recharge_hist.clear()
//        rechargeRef.removeEventListener(this)
//        rechargeRef.addChildEventListener(this)
//        adapter.notifyDataSetChanged()
//        if (recharge_hist.size == 0){
//            Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
//        }
//    }
//
//    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//        recharge_hist.add(p0.getValue(CookModel::class.java)!!)
//        adapter.notifyDataSetChanged()
//    }
//
//    override fun onChildRemoved(p0: DataSnapshot) {
//        recharge_hist.clear()
//        rechargeRef.removeEventListener(this)
//        rechargeRef.addChildEventListener(this)
//        adapter.notifyDataSetChanged()
//        if (recharge_hist.size == 0){
//            Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
//        }
//    }
}