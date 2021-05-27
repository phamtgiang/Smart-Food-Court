package com.example.bkfoodcourt.model

class OwnerModel {
    var email : String? = ""
    var name : String? = ""
    var phone : String? = ""
    var type : String? = "Owner"
    var uid : String? = ""
    var active : Boolean = false
    var vendorID : Int? = null
    var cook_register_request : ArrayList<Cook> = ArrayList<Cook>()

    constructor(){}
}