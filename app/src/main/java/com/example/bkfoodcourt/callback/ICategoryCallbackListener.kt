package com.example.bkfoodcourt.callback

import com.example.bkfoodcourt.model.CategoryModel

interface ICategoryCallbackListener {
    fun onCategorySuccess(categoriesList:List<CategoryModel>)
    fun onCategoryLoadFailed(message:String)
}