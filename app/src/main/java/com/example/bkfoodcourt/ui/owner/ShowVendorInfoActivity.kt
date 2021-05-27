package com.example.bkfoodcourt.ui.owner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.model.OwnerModel
import com.example.bkfoodcourt.model.VendorModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_show_vendor_info.*

class ShowVendorInfoActivity : AppCompatActivity() {
    val uid = FirebaseAuth.getInstance().currentUser!!.uid.toString()
//    val textViewOwnerName : TextView = findViewById<TextView>(textViewOwnerName)
//    val textViewOwnerID : TextView = findViewById<TextView>(textViewOwnerID)
//    val textViewVendorName : TextView = findViewById<TextView>(textViewVendorName)
//    val textViewVendorID : TextView = findViewById<TextView>(textViewVendorID)
//    val textViewNumEmployee : TextView = findViewById<TextView>(textViewNumEmployee)
//    val textViewNumFood : TextView = findViewById<TextView>(textViewNumFood)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_vendor_info)

//        var textViewEmployeeDetail = findViewById<TextView>(textViewEmployeeDetail)
//        var textViewFoodDetail = findViewById<TextView>(textViewFoodDetail)

        textViewEmployeeDetail.setOnClickListener {
            startActivity(Intent(this, ShowEmployeeListActivity::class.java))
        }

        textViewFoodDetail.setOnClickListener {
            startActivity(Intent(this, ShowFoodListActivity::class.java))
        }

        getAndShowInfo()
    }

    private fun getAndShowInfo(){
        Common.myRef.child("Owners").child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var cur_owner = p0.getValue(OwnerModel::class.java)
                Log.d("------------------DEBUG-----owner------------", cur_owner.toString())
                textViewOwnerName.text = "Owner: " + cur_owner?.name
                textViewOwnerID.text = "ID: " + cur_owner?.uid.toString()
                textViewVendorID.text = "Vendor's ID: " + cur_owner?.vendorID.toString()

                Common.myRef.child("Vendors").child(cur_owner?.vendorID.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var cur_vendor = p0.getValue(VendorModel::class.java)
                        Log.d("------------------DEBUG----------vendor-------", cur_vendor.toString())
                        textViewVendorName.text = "Vendor's Name: " + cur_vendor?.name
                        if (cur_vendor?.employees == null)
                            textViewNumEmployee.text = "Employees: 0"
                        else
                            textViewNumEmployee.text = "Employees: " + cur_vendor?.employees.size.toString()
                        if (cur_vendor?.foods == null)
                            textViewNumFood.text = "Foods: 0"
                        else
                            textViewNumFood.text = "Foods: "+ cur_vendor?.foods.size.toString()
                    }
                })
            }
        })
    }
}