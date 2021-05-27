package com.example.bkfoodcourt.ui.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.example.bkfoodcourt.MainActivity
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.adapter.CustomPagerAdater
import com.example.bkfoodcourt.model.PagerItem
import com.example.bkfoodcourt.ui.ComingSoonActivity
import com.example.bkfoodcourt.ui.owner.ShowEmployeeRegisterRequestActivity
import com.example.bkfoodcourt.ui.owner.ShowVendorInfoActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_manager_home.*
import kotlinx.android.synthetic.main.activity_owner_home.*
import kotlinx.android.synthetic.main.activity_owner_home.buttonSelect
import kotlinx.android.synthetic.main.activity_owner_home.indicatorContainer
import kotlinx.android.synthetic.main.activity_owner_home.viewPager

class OwnerHomeActivity : AppCompatActivity() {

    private val slideAdapter = CustomPagerAdater(
        listOf(
            PagerItem("Wellcome!", "welcome-white.json", ""),
            PagerItem("Request list", "request_list.json", "See list of register request of your vendor"),
            PagerItem("Information", "information.json", "See information of your vendor"),
            PagerItem("Log out", "peas-playground-of-love.json", "Log out of the application")
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_home)

        /**Setup view Pager**/
        viewPager.adapter = slideAdapter
        setupIndicator()
        setCurrentIndicator(0)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        buttonSelect.setOnClickListener{
            var idx = viewPager.currentItem
            if (idx == 0){
                viewPager.currentItem += 1;
            }
            else if (idx == 1){
                startActivity(Intent(this, ShowEmployeeRegisterRequestActivity::class.java))
            }
            else if (idx == 2){
                startActivity(Intent(this, ShowVendorInfoActivity::class.java))
            }
            else{
                showAlertDialog()
            }
        }
    }

    private fun setupIndicator(){
        updateUI()
        val indicators = arrayOfNulls<ImageView>(slideAdapter.itemCount)
        val layoutParams : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices){
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            indicatorContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index : Int){
        updateUI()
        val childCount = indicatorContainer.childCount
        for (i in 0 until childCount){
            val imageView = indicatorContainer[i] as ImageView
            if (i == index){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            }else{
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }
    private fun updateUI(){
        var idx = viewPager.currentItem
        if (idx == 0){
            buttonSelect.text = "Next"
        }
        else{
            buttonSelect.text = "Select"
        }
    }

    private fun showAlertDialog(){
        var layoutInflater= LayoutInflater.from(this)
        var view= layoutInflater.inflate(R.layout.custom_alert_dialog, null)
        var alertDialog= AlertDialog.Builder(this)
        alertDialog.setView(view)

        var customDialog = alertDialog.create()
        var textAccept = view.findViewById<TextView>(R.id.textViewAccept)
        textAccept.text = "Yes"
        var textReject = view.findViewById<TextView>(R.id.textViewReject)
        textReject.text = "No"
        var title = view.findViewById<TextView>(R.id.textViewDialogTitle)
        title.text = getString(R.string.logout_or_not)
        textAccept.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            customDialog.dismiss()
        }

        textReject.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
        customDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.getWindow()?.setGravity(Gravity.CENTER_VERTICAL)
    }
}