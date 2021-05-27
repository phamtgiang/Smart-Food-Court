package com.example.bkfoodcourt.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.bkfoodcourt.MainActivity
import com.example.bkfoodcourt.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed(Runnable{
            val loginIntent = Intent(this, MainActivity::class.java)
            startActivity(loginIntent)
            finish()
        }, 3000)
    }
}