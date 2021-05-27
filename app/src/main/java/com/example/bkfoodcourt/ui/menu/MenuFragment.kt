package com.example.bkfoodcourt.ui.menu

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.common.SpacesItemDecoration
import com.example.bkfoodcourt.adapter.MyCategoriesAdapter
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.model.CategoryModel

class MenuFragment : Fragment() {

    private lateinit var menuViewModel: MenuViewModel
    private lateinit var dialog : AlertDialog
    private lateinit var layoutAnimationController : LayoutAnimationController
    private var adapter: MyCategoriesAdapter? = null
    private var recycler_menu:RecyclerView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        menuViewModel =
            ViewModelProviders.of(this).get(MenuViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_category, container, false)

        initViews(root);
        menuViewModel.getMessageError().observe(this, Observer {
           Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })
        menuViewModel.getCategoryList().observe(this, Observer {
            adapter = MyCategoriesAdapter(
                requireContext(),
                it as List<CategoryModel>
            )
            recycler_menu!!.adapter = adapter
        })
        return root
    }

    private  fun initViews(root:View){
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        recycler_menu = root.findViewById(R.id.recycler_menu) as RecyclerView
        recycler_menu!!.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.orientation = RecyclerView.VERTICAL
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if (adapter != null)
                {
                    when (adapter!!.getItemViewType(position)) {
                        Common.DEFAULT_COLUMN_COUNT -> 1
                        Common.FULL_WIDTH_CONLUMN -> 2
                        else -> -1
                    }

                }else -1
            }
        }
        recycler_menu!!.layoutManager = layoutManager
        recycler_menu!!.addItemDecoration(SpacesItemDecoration(8))

    }
}