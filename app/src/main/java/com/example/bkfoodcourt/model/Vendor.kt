package com.example.bkfoodcourt.model

import com.example.bkfoodcourt.model.FoodModel

data class Vendor (
    var name : String? = "",
    var foods : ArrayList<String> = ArrayList<String>(),
    var employees : ArrayList<String> = ArrayList<String>(),
    var vendorID : Int? = null,
    var ownerID : String? = ""
)