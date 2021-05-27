package com.example.bkfoodcourt.ui.owner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.adapter.CustomListviewAdapter
import com.example.bkfoodcourt.model.CookModel
import com.example.bkfoodcourt.model.OwnerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_show_employee_list.*
import kotlinx.android.synthetic.main.activity_show_employee_register_request.*

class ShowEmployeeListActivity : AppCompatActivity(), ChildEventListener {

    var employeeList = ArrayList<CookModel>()
    var uidList = ArrayList<String>()
    val uid = FirebaseAuth.getInstance().currentUser!!.uid.toString()
    var cur_owner : OwnerModel? = null
    lateinit var adapter : CustomListviewAdapter
    var select : CookModel? = null
    var selectPosition : Int? = null
    lateinit var employeeRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_employee_list)

        /** Get current owner **/
        Common.myRef.child("Owners").child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                cur_owner = p0.getValue(OwnerModel::class.java)
                Log.d("--------------- DEBUG  CUR OWNER------------", cur_owner?.uid.toString())

                /** Init employeeRef**/
                employeeRef = Common.myRef.child("Vendors").child(cur_owner?.vendorID.toString()).child("employees")
                Log.d("--------------------DEBUG EM REF-----------", employeeRef.toString())

                adapter = CustomListviewAdapter(this@ShowEmployeeListActivity, R.layout.custom_listview_item, employeeList)
                listView_employee.adapter = adapter

                /** Get vendor**/
                employeeRef.addChildEventListener(this@ShowEmployeeListActivity)

                listView_employee.setOnItemClickListener { adapter, view, i, l ->
                    select = employeeList[i]
                    selectPosition = i
//            showAlertDialog()
                }
            }
        })
    }

    override fun onCancelled(p0: DatabaseError) {
    }

    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
    }

    override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        uidList.clear()
        employeeRef.removeEventListener(this)
        employeeRef.addChildEventListener(this)
        adapter.notifyDataSetChanged()
        if (uidList.size == 0){
            Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
        }
    }

    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
        val uid_add = p0.getValue().toString()
        Log.d("-------------------DEBUG UID ADD-------------", uid_add)
        uidList.add(p0.getValue().toString())
        Log.d("---------------DEBUG----------UID LIST-------", uidList.toString())
        Common.myRef.child("Cooks").child(uid_add).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                employeeList.add(p0.getValue(CookModel::class.java)!!)
                Log.d("---------------DEBUG----------EM LIST-------", employeeList.toString())
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onChildRemoved(p0: DataSnapshot) {
        uidList.clear()
        employeeRef.removeEventListener(this)
        employeeRef.addChildEventListener(this)
        adapter.notifyDataSetChanged()
        if (uidList.size == 0){
            Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
        }
    }
}