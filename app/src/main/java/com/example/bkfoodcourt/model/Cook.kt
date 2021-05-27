package com.example.bkfoodcourt.model

data class Cook (
    var email : String? = "",
    var name : String? = "",
    var phone : String? = "",
    var type : String? = "",
    var uid : String? = "",
    var active : Boolean = false,
    var vendorID : Int? = null,
    var ownerID : String? = ""
)