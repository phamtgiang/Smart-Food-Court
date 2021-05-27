package com.example.bkfoodcourt.callback

import com.example.bkfoodcourt.model.PopularCategoryModel

interface iPopularLoadCallback {
    fun onPopularLoadSuccess(popularModelList:List<PopularCategoryModel>)
    fun onPopularLoadFailed(message:String)
}