package com.example.bkfoodcourt.callback

import com.example.bkfoodcourt.model.CommentModel
import com.example.bkfoodcourt.model.Order
import java.util.concurrent.TimeUnit

interface ILoadTimeFromFirebaseCallback {
    fun onLoadTimeSuccess(order:Order,estimateTimeMs:Long)
    fun onLoadTimeFailed(message:String)
}