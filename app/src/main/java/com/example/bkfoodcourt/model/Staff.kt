package com.example.bkfoodcourt.model

data class Staff (
    var email : String? = "",
    var name : String? = "",
    var phone : String? = "",
    var type : String? = "Staff",
    var uid : String? = "",
    var active : Boolean = false
)