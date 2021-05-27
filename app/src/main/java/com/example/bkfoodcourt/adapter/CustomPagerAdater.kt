package com.example.bkfoodcourt.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.model.PagerItem
import com.squareup.picasso.Picasso
import java.net.URI
import java.net.URL

class CustomPagerAdater(private val slides : List<PagerItem>) :
    RecyclerView.Adapter<CustomPagerAdater.slideViewHolder>() {



    inner class slideViewHolder(view : View) : RecyclerView.ViewHolder(view){
        private val funcName = view.findViewById<TextView>(R.id.funcName)
        private val animation = view.findViewById<LottieAnimationView>(R.id.pagerAnimation)
        private val description = view.findViewById<TextView>(R.id.funcDescription)
        private val img = view.findViewById<ImageView>(R.id.imageViewFoodDetailItem)

        fun bind(slide : PagerItem){
            funcName.text = slide.funcName
//            Log.d("---------------url---------", slide.animation.toString())
            if (slide.animation.toString().contains("json")){
                img.visibility = View.GONE
                animation.visibility = View.VISIBLE
                animation.setAnimation(slide.animation)
            }
            else{
                Picasso.get().load(slide.animation).into(img)
                animation.visibility = View.GONE
                img.visibility = View.VISIBLE
            }
            description.text = slide.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): slideViewHolder {
        return slideViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.custom_pager,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return slides.size
    }

    override fun onBindViewHolder(holder: slideViewHolder, position: Int) {
        holder.bind(slides[position])
    }
}