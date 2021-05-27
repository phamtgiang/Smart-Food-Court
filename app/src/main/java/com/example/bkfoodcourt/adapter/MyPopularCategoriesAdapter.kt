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
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.model.PopularCategoryModel
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.eventbus.PopularFoodItemClick
import org.greenrobot.eventbus.EventBus

class MyPopularCategoriesAdapter(internal var context: Context, internal var popularCategoryModel: List<PopularCategoryModel>) : RecyclerView.Adapter<MyPopularCategoriesAdapter.MyViewHolder> () {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        override fun onClick(p0: View?) {
            listener!!.onItemClick(p0!!, adapterPosition)
        }

        var category_name:TextView? = null
        var category_price:TextView? = null

        var category_image:ImageView? = null

        internal var listener: IRecyclerItemClickListener?=null

        fun setListener( listener: IRecyclerItemClickListener){
            this.listener=listener;
        }
        init {
            category_name = itemView.findViewById(R.id.txt_category_name) as TextView
            category_image = itemView.findViewById(R.id.category_image) as ImageView
            category_price = itemView.findViewById(R.id.category_price) as TextView
            itemView.setOnClickListener(this)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_popular_categories_item, parent, false))
    }

    override fun getItemCount(): Int {
        return popularCategoryModel.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(popularCategoryModel.get(position).image).into(holder.category_image!!)
        holder.category_name!!.setText(popularCategoryModel.get(position).name)
        holder.category_price!!.setText(popularCategoryModel.get(position).price?.let {
            Common.formatPrice(
                it
            )
        } + "VND")

        holder.setListener(object : IRecyclerItemClickListener{
            override fun onItemClick(view: View, pos: Int) {
                EventBus.getDefault().postSticky(PopularFoodItemClick(popularCategoryModel[pos]))
            }

        })
    }
}