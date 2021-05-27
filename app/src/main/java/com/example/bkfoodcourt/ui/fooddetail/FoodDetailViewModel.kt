package com.example.bkfoodcourt.ui.fooddetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bkfoodcourt.common.Common.foodSelected
import com.example.bkfoodcourt.model.CommentModel
import com.example.bkfoodcourt.model.FoodModel
import com.google.android.gms.common.internal.service.Common

class FoodDetailViewModel : ViewModel() {

    private var mutableLiveDataFood:MutableLiveData<FoodModel>?=null
    private var mutableLiveDataComment:MutableLiveData<CommentModel>?=null

    init {
        mutableLiveDataComment = MutableLiveData()
    }

    fun getMutableLiveDataFood():MutableLiveData<FoodModel> {
        if (mutableLiveDataFood == null) {
            mutableLiveDataFood = MutableLiveData()

        }
        mutableLiveDataFood!!.value = foodSelected
        return mutableLiveDataFood!!
    }

    fun getMutableLiveDataComment():MutableLiveData<CommentModel> {
        if (mutableLiveDataComment == null) {
            mutableLiveDataComment= MutableLiveData()
        }
        return mutableLiveDataComment!!
    }

    fun setCommentModel(commentModel: CommentModel) {
        if (mutableLiveDataComment != null) {
            mutableLiveDataComment!!.value= commentModel
        }
    }

    fun setFoodModel(foodModel: FoodModel) {
        if (mutableLiveDataFood != null) {
            mutableLiveDataFood!!.value = foodModel
        }
    }

}