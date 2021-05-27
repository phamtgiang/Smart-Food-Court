package com.example.bkfoodcourt.ui.manager

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.common.CommonUtils
import com.example.bkfoodcourt.model.RechargeModel
import com.example.bkfoodcourt.model.User
import com.example.bkfoodcourt.model.UserModel
import com.example.bkfoodcourt.ui.ComingSoonActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_recharge_balance_account.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class RechargeBalanceAccountActivity : AppCompatActivity() {

    private var cur_pin : String? = null
    private var cus_email : String? = null
    private var amount : String? = null
    private var cus_pin : String? = null
    private var customer : UserModel? = null
    private val pinRef = Common.myRef.child("PINCode")
    private val userRef = Common.myRef.child("Users")
    private var loading_Dialog : Dialog? = null
    val rechargeRef = Common.myRef.child("RechargeHistory")
    var cur_time : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recharge_balance_account)

        buttonOK.setOnClickListener {
            cus_email = editTextEmailRecharge.text.toString()
            amount = editTextAmound.text.toString()
            cus_pin = editTextPIN.text.toString()
            recharge()
        }

        textViewRechargeHistory.setOnClickListener {
            startActivity(Intent(this, SeeRechargeHistoryActivity::class.java))
        }
    }

    private fun recharge(){
        if (cus_email!!.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.enter_email_error), Toast.LENGTH_SHORT).show()
        }
        else if (amount!!.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.enter_amount), Toast.LENGTH_SHORT).show()
        }
        else if (cus_pin!!.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.enter_pin_code), Toast.LENGTH_SHORT).show()
        }
        else if (cus_pin!!.length != 4){
            Toast.makeText(this, getString(R.string.invalid_pin), Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(cus_email).matches()){
            Toast.makeText(this, getString(R.string.invalid_email_error), Toast.LENGTH_SHORT).show()
        }
        else if ((amount.toString()).toDouble() % 500 != 0.0){
            Toast.makeText(this, getString(R.string.invalid_amount), Toast.LENGTH_SHORT).show()
        }
        else{
            showLoading()
            pinRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    cur_pin = p0.getValue().toString()
                    var userList : ArrayList<UserModel> = ArrayList<UserModel>()
//                    Toast.makeText(this@RechargeBalanceAccountActivity, "Complete get pin: " + cur_pin, Toast.LENGTH_SHORT).show()
                    if (cus_pin == cur_pin) {
                        Log.d(
                            "----------DEBUG = --------------",
                            cus_pin.toString() + " " + cur_pin.toString()
                        )
//                        Toast.makeText(this@RechargeBalanceAccountActivity, "equal", Toast.LENGTH_SHORT).show()
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                var flag : Boolean = false
                                for (child in p0.children){
                                    var cur_user = child.getValue(UserModel::class.java)
                                    Log.d("-----------DEBUG CHILD----------", cur_user.toString())
                                    Log.d("email: ", cur_user!!.email.toString())
                                    if (cur_user!!.email.toString() == cus_email){
                                        flag = true
                                        var cur_balance = cur_user.balance as Double
                                        var addVal : Double = (amount.toString()).toDouble()
                                        cur_balance = cur_balance + addVal
                                        userRef.child(cur_user!!.uid.toString()).child("balance").setValue(cur_balance)
                                        cur_time = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)).toString()
                                        addToHistory(RechargeModel(cur_user!!.email.toString(), cur_time!!, addVal))
                                    }
                                }
                                if (flag == false){
                                    hideLoading()
                                    Toast.makeText(this@RechargeBalanceAccountActivity, getString(R.string.not_member), Toast.LENGTH_LONG).show()
                                }
                            }

                        })
                    }
                }
            })
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

    private fun addToHistory(recharge : RechargeModel){
        var recharge_list : ArrayList<RechargeModel> = ArrayList<RechargeModel>()
        recharge_list.add(recharge)
        rechargeRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (child in p0.children){
                    val cur_recharge = child.getValue(RechargeModel::class.java)
                    recharge_list.add(cur_recharge!!)
                }
                rechargeRef.setValue(recharge_list)
                hideLoading()
                Toast.makeText(this@RechargeBalanceAccountActivity, getString(R.string.recharge_success), Toast.LENGTH_LONG).show()
            }
        })
    }
}