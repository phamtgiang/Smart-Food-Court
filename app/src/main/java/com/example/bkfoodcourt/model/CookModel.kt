package com.example.bkfoodcourt.model

class CookModel {
    var email : String? = ""
    var name : String? = ""
    var phone : String? = ""
    var type : String? = "Cook"
    var uid : String? = ""
    var active : Boolean = false
    var vendorID : Int? = null

    constructor(){}

    constructor(email: String, name: String, phone: String, uid: String, vendorID: Int?){
        this.email = email
        this.name = name
        this.phone = phone
        this.uid = uid
        this.vendorID = vendorID
    }
}