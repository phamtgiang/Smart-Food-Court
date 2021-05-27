package com.example.bkfoodcourt.ui.foodlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bkfoodcourt.model.FoodModel
import com.google.android.gms.common.internal.service.Common

class FoodListViewModel :ViewModel(){

  private  var mutableFoodModelListData : MutableLiveData<List<FoodModel>>?=null

    fun getMutableFoodModelListData(): MutableLiveData<List<FoodModel>>{
        if(mutableFoodModelListData==null)
            mutableFoodModelListData=MutableLiveData()
        mutableFoodModelListData!!.value= com.example.bkfoodcourt.common.Common.categorySelected!!.foods
        return mutableFoodModelListData!!

    }
}