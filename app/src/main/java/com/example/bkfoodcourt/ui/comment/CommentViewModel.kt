package com.example.bkfoodcourt.ui.comment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bkfoodcourt.model.CommentModel

class CommentViewModel : ViewModel() {
    val mutableLiveDataCommentList:MutableLiveData<List<CommentModel>> = MutableLiveData()
    fun setCommentList(commentList: List<CommentModel>)
    {
        mutableLiveDataCommentList.value = commentList
    }
}