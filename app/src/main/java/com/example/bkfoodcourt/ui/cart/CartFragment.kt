package com.example.bkfoodcourt.ui.cart

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bkfoodcourt.adapter.MyCartAdapter
import com.example.bkfoodcourt.callback.IMyButtonCallback
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.common.MySwipeHelper
import com.example.bkfoodcourt.database.CartDataSource
import com.example.bkfoodcourt.database.CartDatabase
import com.example.bkfoodcourt.database.LocalCartDataSource
import com.example.bkfoodcourt.eventbus.CountCartEvent
import com.example.bkfoodcourt.eventbus.HideFABCart
import com.example.bkfoodcourt.eventbus.UpdateItemInCart
import com.example.bkfoodcourt.HomeActivity
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.callback.ILoadTimeFromFirebaseCallback
import com.example.bkfoodcourt.model.Order
import com.example.bkfoodcourt.ui.momo.MomoPayment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class CartFragment : Fragment(), ILoadTimeFromFirebaseCallback {
    // TODO: Rename and change types of parameters
    private var cartDataSource:CartDataSource?=null
    private var compositeDisposable:CompositeDisposable= CompositeDisposable()
    private var recyclerViewState:Parcelable?=null
    private lateinit var cartViewModel: CartViewModel
    private lateinit var btn_place_order:Button

    var txt_empty_cart:TextView?=null
    var txt_total_price:TextView?=null
    var group_place_holder:CardView?=null
    var recycler_cart:RecyclerView?=null
    var adapter:MyCartAdapter?=null
    lateinit var listener : ILoadTimeFromFirebaseCallback


    override fun onResume() {
        super.onResume()
        calculateTotalPrice()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cartDataSource=LocalCartDataSource(CartDatabase.getInstance(requireContext()!!).cartDAO())
        EventBus.getDefault().postSticky(HideFABCart(true))
        cartViewModel=ViewModelProviders.of(this).get(CartViewModel::class.java)
        cartViewModel.initCartdataSource(requireContext())

        val root= inflater.inflate(R.layout.fragment_cart, container, false)
        initViews(root)
        cartViewModel.getMutableLiveDataCartItem().observe(viewLifecycleOwner, Observer {
            if (it==null || it.isEmpty())
            {
                recycler_cart!!.visibility=View.GONE
                group_place_holder!!.visibility=View.GONE
                txt_empty_cart!!.visibility=View.VISIBLE

            }
            else
            {
                recycler_cart!!.visibility=View.VISIBLE
                group_place_holder!!.visibility=View.VISIBLE
                txt_empty_cart!!.visibility=View.GONE
                adapter = MyCartAdapter(requireContext(),it)
                recycler_cart!!.adapter=adapter
            }
        })
        // Inflate the layout for this fragment
        return root
    }

    private fun initViews(root:View) {
        setHasOptionsMenu((true))

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(requireContext()).cartDAO())
        listener = this
        recycler_cart=root.findViewById(R.id.recycler_cart) as RecyclerView
        recycler_cart!!.setHasFixedSize(true)
        val layoutManager=LinearLayoutManager(context)
        recycler_cart!!.layoutManager=layoutManager
        recycler_cart!!.addItemDecoration(DividerItemDecoration(context,layoutManager.orientation))

        //recycler view 17
        val swipe = object :MySwipeHelper(requireContext(),recycler_cart!!,200)
        {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(context!!,"Delete",30,0,Color.parseColor("#FF0000"),
                    object:IMyButtonCallback{
                        override fun onClick(pos: Int) {
                            val deleteItem = adapter!!.getItemAtPosition(pos)
                            cartDataSource!!.deleteCart(deleteItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object :SingleObserver<Int>{
                                    override fun onSuccess(t: Int) {
                                        adapter!!.notifyItemRemoved(pos)
                                        sumCart()
                                        EventBus.getDefault().postSticky(CountCartEvent(true))
                                        Toast.makeText(context,"Delete item success",Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onSubscribe(d: Disposable) {
                                    }

                                    override fun onError(e: Throwable) {
                                        Toast.makeText(context,""+e.message,Toast.LENGTH_SHORT).show()
                                    }

                                })

                        }

                    }))
            }
        }

        txt_empty_cart=root.findViewById(R.id.txt_empty_cart) as TextView
        txt_total_price=root.findViewById(R.id.txt_total_price) as TextView
        group_place_holder= root.findViewById(R.id.group_place_holder) as CardView


        btn_place_order=root.findViewById(R.id.btn_place_order) as Button
        //Event for place button place order
        btn_place_order.setOnClickListener{
            val builder= AlertDialog.Builder(requireContext())
            builder.setTitle("Choose Payment Method")
            val view=LayoutInflater.from(context).inflate(R.layout.layout_payment_method,null)
            builder.setView(view)
            val pDialog=builder.create()
            pDialog.show()
            var paymentButton : ImageButton = view.findViewById(R.id.imPayment)
            paymentButton.setOnClickListener {
                val remainingBalance = Common.currentUser!!.balance - Common.order!!.finalPayment
                if(remainingBalance < 0) {
                    Toast.makeText(context, "The balance is not enough, Please top up to make payment",Toast.LENGTH_SHORT).show()
                }
                else {
                    val builder= AlertDialog.Builder(requireContext())
                    builder.setTitle("Reconfirm Order")
                    val view=LayoutInflater.from(context).inflate(R.layout.layout_place_order,null)
                    val paymentView : TextView = view.findViewById(R.id.txt_total_payment)
                    val balanceView : TextView = view.findViewById(R.id.txt_remaining_balance)
                    paymentView.text = StringBuilder("Total Price: \n").append(Common.formatPrice(Common.order!!.finalPayment)).append(" VND")
                    balanceView.text = StringBuilder("The remaining balance: \n").append(Common.formatPrice(remainingBalance)).append(" VND")
                    builder.setView(view)
                    builder.setNegativeButton("Cancel") { dialogInterface, _    -> dialogInterface.dismiss() }
                        .setPositiveButton("Ok") { dialogInterface, _   ->
                            makePayment()
                            updateBalance(remainingBalance)
                            //placedOrder()
                        }
                    val dialog=builder.create()
                    dialog.show()
                    pDialog.dismiss()
                }
            }

            var paymentMomo : ImageButton = view.findViewById(R.id.imMoMo)
            paymentMomo.setOnClickListener {
                startActivity(Intent(context, MomoPayment::class.java))
            }
        }
    }

    private fun updateBalance(remainingBalance:Double) {
        Common.currentUser!!.balance = remainingBalance
/*        var textView : TextView  = getLayoutInflater().inflate(R.id.nav_info, null).findViewById(R.id.txt_balance)
        Common.setSpanString("Balance: ",Common.formatPrice(remainingBalance) +" VND",textView)*/
        FirebaseDatabase.getInstance()
            .getReference(Common.USER_REFERENCE)
            .child(Common.currentUser!!.uid!!)
            .child("balance")
            .setValue(remainingBalance)
            .addOnFailureListener{e -> Toast.makeText(requireContext(), ""+e.message, Toast.LENGTH_SHORT).show()}
            .addOnCompleteListener{

            }
    }

    private fun makePayment(){
        compositeDisposable.add(cartDataSource!!.getAllCart(Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({cartItemList ->
                cartDataSource!!.sumPrice(Common.currentUser!!.uid!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Double>{
                        override fun onSuccess(t: Double) {
                            val order = Common.order!!
                            order.cartListItem = cartItemList
                            //Submit to firebase
                            syncLocalTimeWithServerTime(order)
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onError(e: Throwable) {
                        }

                    })


            },{throwable -> Toast.makeText(requireContext(), ""+throwable.message, Toast.LENGTH_SHORT).show() })
        )
    }

    private fun syncLocalTimeWithServerTime(order: Order) {
        val offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset")
        offsetRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                listener.onLoadTimeFailed(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                val offset = p0.getValue(Long::class.java)
                val estimateTimeMs = System.currentTimeMillis() + offset!!
                val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm")
                val date = Date(estimateTimeMs)
                listener.onLoadTimeSuccess(order, estimateTimeMs)
            }

        })
    }

    private fun writeOrderToFireBase(order: Order) {
        FirebaseDatabase.getInstance()
            .getReference(Common.ORDER_REF)
            .child(Common.createOrderNumber())
            .setValue(order)
            .addOnFailureListener{e -> Toast.makeText(requireContext(), ""+e.message, Toast.LENGTH_SHORT).show()}
            .addOnCompleteListener{task ->
                //Clean cart
                if(task.isSuccessful)
                {
                    cartDataSource!!.cleanCart(Common.currentUser!!.uid!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object :SingleObserver<Int>{
                            override fun onSuccess(t: Int) {
                                Toast.makeText(context!!, "Order placed successfully", Toast.LENGTH_SHORT).show()
                                EventBus.getDefault().postSticky(CountCartEvent(true))
                            }

                            override fun onSubscribe(d: Disposable) {
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(context!!, "" + e.message, Toast.LENGTH_SHORT).show()
                            }

                        })
                }

            }
    }

    private fun sumCart() {
        cartDataSource!!.sumPrice(Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<Double>{
                override fun onSuccess(t: Double) {
                    txt_total_price!!.text = StringBuilder("Total: ")
                        .append(t)
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    //if(!e.message!!.contains("Query returned empty"))
                    //Toast.makeText(context,""+e.message!!,Toast.LENGTH_SHORT).show()
                }

            })
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        cartViewModel!!.onStop()
        compositeDisposable.clear()
        EventBus.getDefault().postSticky(HideFABCart(false))
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    @Subscribe (sticky = true, threadMode = ThreadMode.MAIN)
    fun onUpdateItemInCart(event:UpdateItemInCart){
        if (event.cartItem!=null)
        {
            recyclerViewState=recycler_cart!!.layoutManager!!.onSaveInstanceState()
            cartDataSource!!.updateCart(event.cartItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object :SingleObserver<Int>{
                    override fun onSuccess(t: Int) {
                        calculateTotalPrice()
                        recycler_cart!!.layoutManager!!.onRestoreInstanceState(recyclerViewState)
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(requireContext(), "[UPDATE CART] "+e.message, Toast.LENGTH_SHORT).show()
                    }

                })
        }
    }

    private fun calculateTotalPrice() {
        cartDataSource!!.sumPrice(Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:SingleObserver<Double>{
                override fun onSuccess(price: Double) {
                    txt_total_price!!.text=StringBuilder("Total: ").append(Common.formatPrice(price)).append(" VND")
                    val order = Order()
                    order.userName = Common.currentUser!!.name!!
                    order.userId = Common.currentUser!!.uid!!
                    order.finalPayment = price
                    Common.order = order

                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    if(!e.message!!.contains("Query returned empty"))
                        Toast.makeText(context, "[SUM CART]"+e.message , Toast.LENGTH_SHORT).show()
                }

            })
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.cart_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item!!.itemId == R.id.action_clear_cart)
        {
            cartDataSource!!.cleanCart(Common.currentUser!!.uid!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object:SingleObserver<Int>{
                    override fun onSuccess(t: Int) {
                        Toast.makeText(context,"Clear Cart",Toast.LENGTH_SHORT)
                        EventBus.getDefault().postSticky(CountCartEvent(true))
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context,""+e.message,Toast.LENGTH_SHORT)
                    }

                })
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun moveToHome() {
        startActivity(Intent(requireContext(), HomeActivity::class.java))
    }

    override fun onLoadTimeSuccess(order: Order, estimateTimeMs: Long) {
        order.createDate = estimateTimeMs
        order.orderStatus = 0
        writeOrderToFireBase(order)
    }

    override fun onLoadTimeFailed(message: String) {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }
}