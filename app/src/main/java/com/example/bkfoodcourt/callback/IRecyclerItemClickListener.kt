package com.example.bkfoodcourt.callback

import android.view.View

interface IRecyclerItemClickListener {
    fun onItemClick(view:View,pos:Int)
}