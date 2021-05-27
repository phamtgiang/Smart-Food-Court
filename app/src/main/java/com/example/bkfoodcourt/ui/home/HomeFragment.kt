package com.example.bkfoodcourt.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.asksira.loopingviewpager.LoopingViewPager
import com.example.bkfoodcourt.adapter.MyBestDealsApdapter
import com.example.bkfoodcourt.adapter.MyPopularCategoriesAdapter
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.model.BestDealModel
import com.example.bkfoodcourt.model.PopularCategoryModel

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    var recyclerPopular:RecyclerView?=null
    var recyclerBestDeal:RecyclerView?=null
    var viewPager:LoopingViewPager?=null
    var unbinder : Unbinder?=null
    var layoutAnimationController : LayoutAnimationController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        unbinder = ButterKnife.bind(this, root)
        initView(root)

        homeViewModel.popularList.observe(viewLifecycleOwner, Observer {
            val listData = it
            val adapter =
                MyPopularCategoriesAdapter(
                    requireContext(),
                    listData as List<PopularCategoryModel>
                )
            recyclerPopular!!.adapter = adapter
            recyclerPopular!!.layoutAnimation = layoutAnimationController
        })

        homeViewModel.bestDealList.observe(viewLifecycleOwner, Observer {
            val listData = it
            val adapter =
                MyBestDealsApdapter(
                    requireContext(),
                    listData as List<BestDealModel>
                )
            recyclerBestDeal!!.adapter = adapter
            recyclerBestDeal!!.layoutAnimation = layoutAnimationController
        })

        /*
        homeViewModel.bestDealList.observe(viewLifecycleOwner, Observer {
            val adapter = MyBestDealsApdapter(
                requireContext(),
                it,
                false
            )
            viewPager!!.adapter = adapter
        })

         */
        return root
    }

    private fun initView(root:View) {
        layoutAnimationController =  AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        //viewPager = root.findViewById(R.id.viewpager) as LoopingViewPager
        recyclerPopular = root.findViewById(R.id.recycler_popular) as RecyclerView
        recyclerPopular!!.setHasFixedSize(true)
        recyclerPopular!!.layoutManager= LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        recyclerBestDeal = root.findViewById(R.id.recycler_best_deal) as RecyclerView
        recyclerBestDeal!!.setHasFixedSize(true)
        recyclerBestDeal!!.layoutManager= LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

}