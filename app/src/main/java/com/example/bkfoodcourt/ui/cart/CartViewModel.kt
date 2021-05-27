package com.example.bkfoodcourt.ui.cart

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bkfoodcourt.database.CartDataSource
import com.example.bkfoodcourt.database.CartDatabase
import com.example.bkfoodcourt.database.CartItem
import com.example.bkfoodcourt.database.LocalCartDataSource
import com.google.android.gms.common.internal.service.Common
import com.google.firebase.database.MutableData
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class CartViewModel: ViewModel() {
    private val compositeDisposable:CompositeDisposable
    private var cartDataSource:CartDataSource?=null
    private var mutableLiveDataCartItem:MutableLiveData<List<CartItem>>?=null

    init {
        compositeDisposable= CompositeDisposable()


    }
    fun initCartdataSource(context: Context){
        cartDataSource=LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())
    }

    fun getMutableLiveDataCartItem(): MutableLiveData<List<CartItem>>{
        if (mutableLiveDataCartItem==null)
            mutableLiveDataCartItem= MutableLiveData()
        getCartItems()
        return mutableLiveDataCartItem!!
    }
    private fun getCartItems(){
        compositeDisposable.addAll(cartDataSource!!.getAllCart(com.example.bkfoodcourt.common.Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({cartItem->
                mutableLiveDataCartItem!!.value=cartItem

            },{t: Throwable? -> mutableLiveDataCartItem!!.value=null }))
    }

    fun onStop(){
        compositeDisposable.clear()
    }

}