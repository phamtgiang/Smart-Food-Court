package com.example.bkfoodcourt.model

data class User(
    val name : String? = "",
    val phone : String? = "",
    val email : String? = "",
    val uid : String? = "",
    val balance : Double = 0.0,
    val type : String? = "",
    val active : Boolean? = null
)