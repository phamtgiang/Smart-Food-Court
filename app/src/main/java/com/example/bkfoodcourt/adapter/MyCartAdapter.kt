package com.example.bkfoodcourt.adapter

import android.content.Context
import android.graphics.ColorSpace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.bkfoodcourt.database.CartDataSource
import com.example.bkfoodcourt.database.CartDatabase
import com.example.bkfoodcourt.database.CartItem
import com.example.bkfoodcourt.database.LocalCartDataSource
import com.example.bkfoodcourt.eventbus.CountCartEvent
import com.example.bkfoodcourt.eventbus.UpdateItemInCart
import com.example.bkfoodcourt.model.FoodModel
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.common.Common
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.layout_cart_item.view.*
import org.greenrobot.eventbus.EventBus
import org.w3c.dom.Text
import kotlin.text.StringBuilder

class MyCartAdapter (internal var context:Context,
                     internal var cartItems:List<CartItem>):
    RecyclerView.Adapter<MyCartAdapter.MyViewHolder>(){

    internal var compositeDisposable:CompositeDisposable
    internal var cartDataSource:CartDataSource

    init {
        compositeDisposable= CompositeDisposable()
        cartDataSource=LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {

        lateinit var img_cart:ImageView
        lateinit var txt_food_name:TextView
        lateinit var txt_food_price:TextView
        lateinit var number_button:ElegantNumberButton

        init {
            img_cart=itemView.findViewById(R.id.img_cart) as ImageView
            txt_food_name=itemView.findViewById(R.id.txt_food_name)
            txt_food_price=itemView.findViewById(R.id.txt_food_price)
            number_button=itemView.findViewById(R.id.number_button)

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item, parent, false))
    }

    override fun getItemCount(): Int {
        return cartItems.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(cartItems[position].foodImage)
            .into(holder.img_cart)
        holder.txt_food_name.text=StringBuilder(cartItems[position].foodName!!)
        holder.txt_food_price.text=StringBuilder("").append(Common.formatPrice(cartItems[position].foodPrice!!)).append(" VND")
        holder.number_button.number=cartItems[position].foodQuantity.toString()

        //Event
        holder.number_button.setOnValueChangeListener{view,oldValue,newValue ->
            cartItems[position].foodQuantity=newValue
            EventBus.getDefault().postSticky(UpdateItemInCart(cartItems[position]))
            EventBus.getDefault().postSticky(CountCartEvent(true))
        }
    }

    fun getItemAtPosition(pos: Int): CartItem {
        return cartItems[pos]
    }

}