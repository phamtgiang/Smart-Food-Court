package com.example.bkfoodcourt.model

data class Owner (
    var email : String? = "",
    var name : String? = "",
    var phone : String? = "",
    var type : String? = "Owner",
    var uid : String? = "",
    var active : Boolean = false,
    var vendorID : String? = "",
    var cook_register_request : ArrayList<String> = ArrayList<String>()
)