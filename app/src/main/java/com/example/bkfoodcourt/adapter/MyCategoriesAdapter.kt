package com.example.bkfoodcourt.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bkfoodcourt.callback.IRecyclerItemClickListener
import com.example.bkfoodcourt.common.Common.categorySelected
import com.example.bkfoodcourt.eventbus.CategoryClick
import com.example.bkfoodcourt.model.CategoryModel
import com.example.bkfoodcourt.R
import org.greenrobot.eventbus.EventBus

class MyCategoriesAdapter(
        internal var context: Context,
        internal var cateGoriesList: List<CategoryModel>
    ) : RecyclerView.Adapter<MyCategoriesAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var category_name: TextView? = null

        var category_image: ImageView? = null

        internal var listener:IRecyclerItemClickListener?=null;
        fun setListener( listener: IRecyclerItemClickListener){
            this.listener=listener;
        }

        init {
            category_name = itemView.findViewById(R.id.category_name) as TextView
            category_image = itemView.findViewById(R.id.category_image) as ImageView
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listener!!.onItemClick(view!!,adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_category_item, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return if(cateGoriesList.size == 1)
            com.example.bkfoodcourt.common.Common.DEFAULT_COLUMN_COUNT
        else {
            if(cateGoriesList.size % 2 == 0)
                com.example.bkfoodcourt.common.Common.DEFAULT_COLUMN_COUNT
            else
                if(position > 1 && position == cateGoriesList.size - 1)
                    com.example.bkfoodcourt.common.Common.FULL_WIDTH_CONLUMN
                else
                    com.example.bkfoodcourt.common.Common.DEFAULT_COLUMN_COUNT
        }
    }

    override fun getItemCount(): Int {
        return cateGoriesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(cateGoriesList.get(position).image).into(holder.category_image!!)
        holder.category_name!!.setText(cateGoriesList.get(position).name)

        //Event
        holder.setListener(object:IRecyclerItemClickListener{
            override fun onItemClick(view: View, pos: Int) {
                categorySelected = cateGoriesList.get(pos)
                EventBus.getDefault().postSticky(CategoryClick(true,cateGoriesList.get(pos)))
            }
        })
    }
}