package com.example.bkfoodcourt.callback

import com.example.bkfoodcourt.model.CategoryModel
import com.example.bkfoodcourt.model.CommentModel

interface ICommentCallback {
    fun onCommentLoadSuccess(commentList:List<CommentModel>)
    fun onCommentLoadFailed(message:String)
}