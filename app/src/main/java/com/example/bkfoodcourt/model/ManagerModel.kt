package com.example.bkfoodcourt.model

class ManagerModel {
    var email : String? = ""
    var name : String? = ""
    var phone : String? = ""
    var type : String? = "Manager"
    var uid : String? = ""
    var active : Boolean = false
    var owner_register_request : ArrayList<Owner> = ArrayList<Owner>()

    constructor(){}
}