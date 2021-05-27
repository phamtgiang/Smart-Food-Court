package com.example.bkfoodcourt.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bkfoodcourt.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        forgot_password_button.setOnClickListener {
            sendNewPassword()
        }
    }

    private fun sendNewPassword(){
        var email = editTextEmailForgot.text.toString()
        var flag : Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (email.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.enter_email_error), Toast.LENGTH_SHORT).show()
        }
        else if (!flag){
            Toast.makeText(this, getString(R.string.invalid_email_error), Toast.LENGTH_SHORT).show()
        }
        else{
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(this, getString(R.string.send_email_success), Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this, getString(R.string.not_member_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}