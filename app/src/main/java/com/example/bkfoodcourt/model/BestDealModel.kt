package com.example.bkfoodcourt.model

class BestDealModel {
    var food_id:String? = null
    var menu_id:String? = null
    var name:String? = null
    var image:String? = null
    var price:Double?=0.0

    constructor()
    constructor(food_id: String?, menu_id: String?, name: String?, image: String?, price: Double?) {
        this.food_id = food_id
        this.menu_id = menu_id
        this.name = name
        this.image = image
        this.price = price
    }
}