package com.example.bkfoodcourt.ui.owner

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.adapter.CustomListviewAdapter
import com.example.bkfoodcourt.model.CookModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_show_employee_register_request.*

class ShowEmployeeRegisterRequestActivity : AppCompatActivity(), ChildEventListener {

    var requestList = ArrayList<CookModel>()
    val uid = FirebaseAuth.getInstance().currentUser!!.uid
    val requestRef = Common.myRef.child("Owners").child(uid).child("registerRequest")
    lateinit var adapter : CustomListviewAdapter
    var select : CookModel? = null
    var selectPosition : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_employee_register_request)

        adapter = CustomListviewAdapter(this, R.layout.custom_listview_item, requestList)
        listView_employee_register_request.adapter = adapter

        requestRef.addChildEventListener(this)

        listView_employee_register_request.setOnItemClickListener { adapter, view, i, l ->
            select = requestList[i]
            selectPosition = i
            showAlertDialog()
        }
    }

    override fun onCancelled(p0: DatabaseError) {
    }

    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
    }

    override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        requestList.clear()
        requestRef.removeEventListener(this)
        requestRef.addChildEventListener(this)
        adapter.notifyDataSetChanged()
        if (requestList.size == 0){
            Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
        }
    }

    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
        requestList.add(p0.getValue(CookModel::class.java)!!)
        adapter.notifyDataSetChanged()
    }

    override fun onChildRemoved(p0: DataSnapshot) {
        requestList.clear()
        requestRef.removeEventListener(this)
        requestRef.addChildEventListener(this)
        adapter.notifyDataSetChanged()
        if (requestList.size == 0){
            Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
        }
    }

    private fun showAlertDialog(){
        var layoutInflater= LayoutInflater.from(this)
        var view= layoutInflater.inflate(R.layout.custom_alert_dialog, null)
        var alertDialog= AlertDialog.Builder(this)
        alertDialog.setView(view)

        var customDialog = alertDialog.create()
        var textAccept = view.findViewById<TextView>(R.id.textViewAccept)
        var textReject = view.findViewById<TextView>(R.id.textViewReject)
        textAccept.setOnClickListener {
            acceptAccount()
            Toast.makeText(this, "Accept account", Toast.LENGTH_SHORT).show()
            customDialog.dismiss()
        }

        textReject.setOnClickListener {
            rejectAccount()
            Toast.makeText(this, "Reject account", Toast.LENGTH_SHORT).show()
            customDialog.dismiss()
        }
        customDialog.show()
        customDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.getWindow()?.setGravity(Gravity.CENTER_VERTICAL)
    }

    private fun acceptAccount(){

        /** Add uid of select account to employees list of vendor**/
        Common.myRef.child("Vendors").child(select!!.vendorID.toString()).child("employees").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var uidList : ArrayList<String> = p0.getValue() as ArrayList<String>
                uidList.add(select!!.uid.toString())
                Common.myRef.child("Vendors").child(select!!.vendorID.toString()).child("employees").setValue(uidList)
            }
        })

        /** Active selected account**/
        Common.myRef.child("Cooks").child(select!!.uid.toString()).child("active").setValue(true)

        /** Delete selected account in register request **/

        requestRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var mutableList = requestList.toMutableList().apply {
                    selectPosition?.let { removeAt(it) }
                }
                requestRef.setValue(mutableList)
            }
        })
    }

    private fun rejectAccount(){
        /** Delete selected account in register request **/

        requestRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var mutableList = requestList.toMutableList().apply {
                    selectPosition?.let { removeAt(it) }
                }
                requestRef.setValue(mutableList)
            }
        })
    }
}