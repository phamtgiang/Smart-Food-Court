package com.example.bkfoodcourt.callback

import com.example.bkfoodcourt.model.BestDealModel
import com.example.bkfoodcourt.model.PopularCategoryModel

interface iBestDealLoadCallBack {
    fun onBestDealLoadSuccess(bestDealList:List<BestDealModel>)
    fun onbBestDealLoadFailed(message:String)
}