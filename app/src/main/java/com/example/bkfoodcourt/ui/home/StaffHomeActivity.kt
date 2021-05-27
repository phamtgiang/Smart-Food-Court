package com.example.bkfoodcourt.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.example.bkfoodcourt.MainActivity
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.adapter.CustomPagerAdater
import com.example.bkfoodcourt.model.PagerItem
import com.example.bkfoodcourt.ui.ComingSoonActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_manager_home.*

class StaffHomeActivity : AppCompatActivity() {

    private val slideAdapter = CustomPagerAdater(
        listOf(
            PagerItem("Wellcome!", "welcome-white.json", ""),
            PagerItem("Shutdown", "shutdown.json", "Shut down whole system"),
            PagerItem("Turn on", "connect.json", "Turn on system"),
            PagerItem("Log out", "peas-playground-of-love.json", "Log out of the application")
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_home)

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
                startActivity(Intent(this, ComingSoonActivity::class.java))
            }
            else if (idx == 2){
                startActivity(Intent(this, ComingSoonActivity::class.java))
            }
            else{
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
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
}