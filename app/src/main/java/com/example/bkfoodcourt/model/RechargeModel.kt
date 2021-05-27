package com.example.bkfoodcourt.model

import java.time.temporal.TemporalAmount

class RechargeModel {
    var email : String? = null
    var time : String? = null
    var amount : Double = 0.0

    constructor(){}

    constructor(email : String, time : String, amount : Double){
        this.email = email
        this.time = time
        this.amount = amount
    }
}