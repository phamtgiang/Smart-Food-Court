package com.example.bkfoodcourt.ui.login

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.bkfoodcourt.common.CommonUtils
import com.example.bkfoodcourt.model.User
import com.example.bkfoodcourt.model.UserModel
import com.example.bkfoodcourt.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.register_layout.*

class RegisterActivity : AppCompatActivity() {
    private var loading_Dialog : Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        register_button.setOnClickListener {
            register_button.visibility = View.GONE
            register()
            register_button.visibility = View.VISIBLE
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

    private fun register(){
        val email = editTextEmailRegister.text.toString()
        val password = editTextPasswordRegister.text.toString()
        val confirm = editTextConfirmPassword.text.toString()
        val name = editTextFullName.text.toString()
        val phone = editTextPhoneNumber.text.toString()
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
        else{
            showLoading()
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid.toString()
                    val user = User(name, phone, email, uid, 0.0)
                    val database = Firebase.database
                    val myRef = database.getReference("Users")
                    myRef.child(uid).setValue(user)
                    hideLoading()
                    //Log.d("------- debug -------", FirebaseAuth.getInstance().currentUser!!.uid.toString())
                    Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                }
                else if (!task.isSuccessful){
                    hideLoading()
                    Toast.makeText(this, getString(R.string.email_already_taken_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}