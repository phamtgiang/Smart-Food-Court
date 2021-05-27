package com.example.bkfoodcourt.database

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class LocalCartDataSource(private val cartDAO: CartDAO) : CartDataSource {
    override fun getAllCart(uid: String): Flowable<List<CartItem>> {
        return cartDAO.getAllCart(uid)
    }

    override fun countItemCart(uid: String): Single<Int> {
        return cartDAO.countItemCart(uid)
    }

    override fun sumPrice(uid: String): Single<Double> {
        return cartDAO.sumPrice(uid)
    }

    override fun getItemInCart(foodId: String, uid: String): Single<CartItem> {
        return cartDAO.getItemInCart(foodId, uid)
    }

    override fun insertOrReplaceAll(vararg cartItems: CartItem): Completable {
        return cartDAO.insertOrReplaceAll(*cartItems)
    }

    override fun updateCart(cart: CartItem): Single<Int> {
        return cartDAO.updateCart(cart)
    }

    override fun deleteCart(cart: CartItem): Single<Int> {
        return cartDAO.deleteCart(cart)
    }

    override fun cleanCart(uid: String): Single<Int> {
        return cartDAO.cleanCart(uid)
    }

    override fun getItemWithAllOptionsInCart(
        uid: String,
        foodId: String
    ): Single<CartItem> {
        return cartDAO.getItemWithAllOptionsInCart(uid, foodId)
    }
}