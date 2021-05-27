package com.example.bkfoodcourt.ui.login

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.common.CommonUtils
import com.example.bkfoodcourt.model.User
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.model.CookModel
import com.example.bkfoodcourt.model.VendorModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register_employee.*

class RegisterEmployeeActivity : AppCompatActivity() {
    var acc_type : String = ""
    var vendorID : Int? = null
    var ownerID : String? = ""
    private var loading_Dialog : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_employee)

        /**Hide spinner choose vendor**/
        spinnerChooseVendor.visibility = View.GONE

        /**Setup spinner choose vendor**/
        var vendorList : ArrayList<VendorModel> = ArrayList<VendorModel>()
        var vendorNameList : ArrayList<String> = ArrayList<String>()
        vendorNameList.add(getString(R.string.choose_your_vendor))
        Common.myRef.child("Vendors").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (child in p0.children){
                    vendorList.add(child.getValue(VendorModel::class.java)!!)
                    vendorNameList.add(child.getValue(VendorModel::class.java)!!.name.toString())
//                    Log.d("----------------DEBUG-----------------", child.getValue().toString())
//                    Log.d("----------------DEBUG-----------------", child.getValue(VendorModel::class.java)!!.name.toString())
                }
//                for (vendor in vendorList){
//                    vendorNameList.add(vendor.name.toString())
//                    Log.d("----------------DEBUG-----------------", vendor.name.toString())
//                }
                val adapter = ArrayAdapter(this@RegisterEmployeeActivity, R.layout.custom_spinner, vendorNameList)
                adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown)
                spinnerChooseVendor.adapter = adapter
            }
        })

        /**Setup spinner account type**/
        var types = resources.getStringArray(R.array.account_type)
        val adapter = ArrayAdapter(this, R.layout.custom_spinner, types)
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown)
        spinnerAccountType.adapter = adapter

        spinnerAccountType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
//                Log.d("-----------------DEBUG-------------", acc_type)
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                acc_type = p0?.getItemAtPosition(p2).toString()
                if (acc_type == "Owner" || acc_type == "Cook")
                    spinnerChooseVendor.visibility = View.VISIBLE
//                Log.d("-----------------DEBUG-------------", acc_type)
                else{
                    spinnerChooseVendor.visibility = View.GONE
                    vendorID = null
                }
            }
        }

        spinnerChooseVendor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 >= 1) {
                    vendorID = vendorList.get(p2 - 1).vendorID
                    ownerID = vendorList.get(p2 - 1).ownerID.toString()
                    Log.d("---------------DEBUG OWNER ID-------------", ownerID!!)
                }
            }
        }

        register_employee_button.setOnClickListener {
            register_employee_button.visibility = View.GONE
            register_employee()
            register_employee_button.visibility = View.VISIBLE
        }
    }

    private fun hideLoading(){
        loading_Dialog?.let {
            if (it.isShowing)
                it.cancel()
        }
    }

    private fun showLoading(){
        hideLoading()
        loading_Dialog = CommonUtils.showLoadingDialog(this)
    }

    private fun register_employee(){
        val email = editTextEmailRegisterEmployee.text.toString()
        val password = editTextPasswordRegisterEmployee.text.toString()
        val confirm = editTextConfirmPasswordEmployee.text.toString()
        val name = editTextFullNameEmployee.text.toString()
        val phone = editTextPhoneNumberEmployee.text.toString()
        if (email.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.enter_email_error), Toast.LENGTH_SHORT).show()
        }
        else if (password.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.password_register_error), Toast.LENGTH_SHORT).show()
        }
        else if (confirm.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.enter_confirm), Toast.LENGTH_SHORT).show()
        }
        else if (name.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.enter_fullname), Toast.LENGTH_SHORT).show()
        }
        else if (phone.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.enter_phone_number), Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, getString(R.string.invalid_email_error), Toast.LENGTH_SHORT).show()
        }
        else if (!password.equals(confirm)){
            Toast.makeText(this, getString(R.string.confirm_password_error), Toast.LENGTH_SHORT).show()
        }
        else if (acc_type.length > 10){
            Toast.makeText(this, getString(R.string.choose_account_type), Toast.LENGTH_SHORT).show()
        }
        else if ((acc_type == "Owner" || acc_type == "Cook") && vendorID == null){
            Toast.makeText(this, getString(R.string.choose_your_vendor), Toast.LENGTH_SHORT).show()
        }
        else{
            showLoading()
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid.toString()
                    val user = User(name, phone, email, uid, 0.0, acc_type, false)
                    val database = Firebase.database
                    val myRef = database.getReference()
                    if (acc_type == "Cook"){
                        val cook = CookModel(email, name, phone, uid, vendorID)

                        myRef.child("Owners").child(ownerID.toString()).child("registerRequest").addListenerForSingleValueEvent(object :
                        ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                var cookRegisterList : ArrayList<CookModel> = ArrayList<CookModel>()
                                for (child in p0.children){
                                    cookRegisterList.add(child.getValue(CookModel::class.java)!!)
                                }
                                cookRegisterList.add(cook)
                                myRef.child("Owners").child(ownerID.toString()).child("registerRequest").setValue(cookRegisterList)
                            }
                        })

                        myRef.child("Cooks").child(uid).setValue(cook)
                    }
                    else if (acc_type == "Manager")
                        myRef.child("Manager").child(uid).setValue(user)
                    else if (acc_type == "Owner")
                        myRef.child("Owners").child(uid).setValue(user)
                    else
                        myRef.child("Staffs").child(uid).setValue(user)
                    hideLoading()
                    //Log.d("------- debug -------", FirebaseAuth.getInstance().currentUser!!.uid.toString())
                    Toast.makeText(this, getString(R.string.employee_register_success), Toast.LENGTH_SHORT).show()
                }
                else if (!task.isSuccessful){
                    hideLoading()
                    Toast.makeText(this, getString(R.string.email_already_taken_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}