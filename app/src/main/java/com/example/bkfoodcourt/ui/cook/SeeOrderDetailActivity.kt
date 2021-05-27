package com.example.bkfoodcourt.ui.cook

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.room.FtsOptions
import androidx.viewpager2.widget.ViewPager2
import com.example.bkfoodcourt.MainActivity
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.adapter.CustomPagerAdater
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.database.CartItem
import com.example.bkfoodcourt.model.Order
import com.example.bkfoodcourt.model.PagerItem
import com.example.bkfoodcourt.ui.ComingSoonActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_manager_home.*
import kotlinx.android.synthetic.main.activity_manager_home.buttonSelect
import kotlinx.android.synthetic.main.activity_manager_home.indicatorContainer
import kotlinx.android.synthetic.main.activity_see_order_detail.*

class SeeOrderDetailActivity : AppCompatActivity() {
    lateinit var slideAdapter : CustomPagerAdater
//        listOf(
//            PagerItem("Wellcome!", "welcome-white.json", ""),
//            PagerItem("Order list", "see_order.json", "See list of orders"),
//            PagerItem("Sold out", "out_of_stock.json", "See the list of sold out items"),
//            PagerItem("Log out", "peas-playground-of-love.json", "Log out of the application")
//        )
    lateinit var adaperList : MutableList<PagerItem>
    private val cur_order : Order? = Common.cur_order
    private val item_list : List<CartItem>? = cur_order?.cartListItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_order_detail)

        /** setup slideAdapter **/
        adaperList = mutableListOf<PagerItem>()
        for (i in 0..(item_list!!.size-1)){
            var name = item_list[i].foodName.toString()
            var img = item_list[i].foodImage
            var id = item_list[i].foodId.toString()
            var item = PagerItem(name, img.toString(), id)
            adaperList.add(item)
        }
        slideAdapter = CustomPagerAdater(adaperList)

        /**Setup view Pager**/
        viewPagerFoodDetail.adapter = slideAdapter
        setupIndicator()
        setCurrentIndicator(0)
        viewPagerFoodDetail.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        buttonSelect.setOnClickListener{
            var idx = viewPagerFoodDetail.currentItem
            if (idx == adaperList.size - 1){
                showAlertDialog()
            }
            else{
                viewPagerFoodDetail.currentItem += 1;
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
        var idx = viewPagerFoodDetail.currentItem
        if (idx == adaperList.size - 1){
            buttonSelect.text = "Process"
        }
        else{
            buttonSelect.text = "Next"
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
        title.text = getString(R.string.process_this_order_or_not)
        textAccept.setOnClickListener {
            startActivity(Intent(this, ComingSoonActivity::class.java))
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