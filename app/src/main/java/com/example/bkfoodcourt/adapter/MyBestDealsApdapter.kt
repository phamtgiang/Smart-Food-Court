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
import com.example.bkfoodcourt.model.BestDealModel
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.eventbus.BestDealItemClick
import org.greenrobot.eventbus.EventBus

class MyBestDealsApdapter(internal var context: Context, internal var bestDealModel : List<BestDealModel>) : RecyclerView.Adapter<MyBestDealsApdapter.MyViewHolder> () {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        override fun onClick(p0: View?) {
            listener!!.onItemClick(p0!!, adapterPosition)
        }

        var best_deal_name:TextView? = null
        var best_deal_price:TextView? = null

        var best_deal_image:ImageView? = null

        internal var listener: IRecyclerItemClickListener?=null

        fun setListener( listener: IRecyclerItemClickListener){
            this.listener=listener;
        }
        init {
            best_deal_name = itemView.findViewById(R.id.txt_best_deal_name) as TextView
            best_deal_image = itemView.findViewById(R.id.best_deal_image) as ImageView
            best_deal_price = itemView.findViewById(R.id.best_deal_price) as TextView
            itemView.setOnClickListener(this)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_best_deals_item, parent, false))
    }

    override fun getItemCount(): Int {
        return bestDealModel.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(bestDealModel.get(position).image).into(holder.best_deal_image!!)
        holder.best_deal_name!!.setText(bestDealModel.get(position).name)
        holder.best_deal_price!!.setText(bestDealModel.get(position).price?.let {
            Common.formatPrice(
                it
            )
        } + "VND")

        holder.setListener(object : IRecyclerItemClickListener{
            override fun onItemClick(view: View, pos: Int) {
                EventBus.getDefault().postSticky(BestDealItemClick(bestDealModel[pos]))
            }

        })
    }
}