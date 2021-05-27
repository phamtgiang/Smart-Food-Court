package com.example.bkfoodcourt.ui.momo

import android.R.attr.fragment
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.common.Common.order
import com.example.bkfoodcourt.database.CartDataSource
import com.example.bkfoodcourt.database.CartDatabase
import com.example.bkfoodcourt.database.LocalCartDataSource
import com.example.bkfoodcourt.eventbus.CountCartEvent
import com.example.bkfoodcourt.HomeActivity
import com.example.bkfoodcourt.R
import com.example.bkfoodcourt.callback.ILoadTimeFromFirebaseCallback
import com.example.bkfoodcourt.model.Order
import com.example.bkfoodcourt.ui.cart.CartFragment
import com.example.bkfoodcourt.ui.home.HomeFragment
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
import org.json.JSONException
import org.json.JSONObject
import vn.momo.momo_partner.AppMoMoLib
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class MomoPayment : AppCompatActivity(), ILoadTimeFromFirebaseCallback {
    private var cartDataSource: CartDataSource?=null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    var tvEnvironment: TextView? = null

    var tvMerchantCode: TextView? = null

    var tvMerchantName: TextView? = null

    var edAmount: TextView? = null

    var tvMessage: TextView? = null

    var btnPayMoMo: Button? = null

    private var amount = Common.order!!.finalPayment
    private val fee = "0"

    private val merchantName = "Ho Chi Minh University of Technology"
    private val merchantCode = "MOMO9P7W20200708"
    private val description = "Thanh toán dịch vụ ăn uống"
    lateinit var listener : ILoadTimeFromFirebaseCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_momo_payment)

        AppMoMoLib.getInstance()
            .setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION

        var button = findViewById<Button>(R.id.btnPayMoMo)
        button.setOnClickListener { // Code here executes on main thread after user presses button
            requestPayment()
        }
        listener = this
        edAmount = findViewById<TextView>(R.id.edAmount)
        edAmount!!.text = "Total: " + Common.formatPrice(amount) + "VND"
        tvMerchantCode = findViewById<TextView>(R.id.tvMerchantCode)
        tvMerchantCode!!.text = "Merchant Code: \n" + merchantCode
        tvMerchantName = findViewById<TextView>(R.id.tvMerchantName)
        tvMerchantName!!.text = "Merchant Name: \n" + merchantName
    }

    //Get token through MoMo app
    private fun requestPayment(): kotlin.Unit {
        vn.momo.momo_partner.AppMoMoLib.getInstance()
            .setAction(vn.momo.momo_partner.AppMoMoLib.ACTION.PAYMENT)
        vn.momo.momo_partner.AppMoMoLib.getInstance()
            .setActionType(vn.momo.momo_partner.AppMoMoLib.ACTION_TYPE.GET_TOKEN)

        val eventValue: kotlin.collections.MutableMap<kotlin.String, kotlin.Any> = HashMap()
        //client Required
        eventValue["merchantname"] = merchantName //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue["merchantcode"] = merchantCode //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue["amount"] = amount//Kiểu integer
        eventValue["orderId"] = "orderId123456789" //uniqueue id cho BillId, giá trị duy nhất cho mỗi BILL
        eventValue["orderLabel"] = "BK Food Court" //gán nhãn

        //client Optional - bill info
        eventValue["merchantnamelabel"] = "Dịch vụ" //gán nhãn
        eventValue["fee"] =  fee//Kiểu integer
        eventValue["description"] = description //mô tả đơn hàng - short description

        //client extra data
        eventValue["requestId"] = merchantCode + "merchant_billId_" + java.lang.System.currentTimeMillis()
        eventValue["partnerCode"] = merchantCode
        //Example extra data
        val objExtraData = JSONObject()
        try {
            objExtraData.put("name", "BK Food Court")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        eventValue["extraData"] = objExtraData.toString()
        eventValue["extra"] = ""
        vn.momo.momo_partner.AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue)
    }

    //Get token callback from MoMo app an submit to server side
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): kotlin.Unit {
        super.onActivityResult(requestCode, resultCode, data)

        var checkMessage : String?=""

        if (requestCode == vn.momo.momo_partner.AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if (data != null) {
                if (data.getIntExtra("status", -1) === 0) {
                    //TOKEN IS AVAILABLE

                    //tvMessage!!.text = "message: " + "Get token " + data.getStringExtra("message")
                    checkMessage = "message: " + "Get token " + data.getStringExtra("message")

                    val token: kotlin.String? = data.getStringExtra("data") //Token response
                    val phoneNumber: kotlin.String? = data.getStringExtra("phonenumber")
                    var env: kotlin.String? = data.getStringExtra("env")
                    if (env == null) {
                        env = "app"
                    }
                    if (token != null && token != "") {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order
                    } else {
                        Toast.makeText(this,"message: " + "Cannot receive information",Toast.LENGTH_SHORT).show()
                    }
                } else if (data.getIntExtra("status", -1) === 1) {
                    //TOKEN FAIL
                    val message =
                        if (data.getStringExtra("message") != null) data.getStringExtra("message") else "Thất bại"
                    Toast.makeText(this,"message: $message",Toast.LENGTH_SHORT).show()
                } else if (data.getIntExtra("status", -1) === 2) {
                    //TOKEN FAIL
                    Toast.makeText(this,"message: " + "Cannot receive information", Toast.LENGTH_SHORT).show()
                } else {
                    //TOKEN FAIL
                    Toast.makeText(this,"message: " + "Cannot receive information",Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this,"message: " + "Cannot receive information",Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this,"message: " + "Cannot get request, please try again",Toast.LENGTH_SHORT).show()

        }

        val intent = Intent(this@MomoPayment, HomeActivity::class.java)

        if (checkMessage!!.contains("Successful")) {

            cartDataSource= LocalCartDataSource(CartDatabase.getInstance(context = this).cartDAO())

            compositeDisposable.add(cartDataSource!!.getAllCart(Common.currentUser!!.uid!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({cartItemList ->
                    cartDataSource!!.sumPrice(Common.currentUser!!.uid!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : SingleObserver<Double>{
                            override fun onSuccess(t: Double) {
                                val order = Common.order
                                order!!.cartListItem = cartItemList

                                syncLocalTimeWithServerTime(order)
                            }

                            override fun onSubscribe(d: Disposable) {
                            }

                            override fun onError(e: Throwable) {
                            }

                        })
                },{throwable -> Toast.makeText(this, ""+throwable.message, Toast.LENGTH_SHORT).show() })
            )

            intent.putExtra("MOMO_MESSAGE", "success")
            startActivity(intent)
            //startActivity(Intent(this@MomoPayment, HomeActivity::class.java))
        }

        else {
            Toast.makeText(this@MomoPayment,"Cannot process transaction", Toast.LENGTH_SHORT).show()

            intent.putExtra("MOMO_MESSAGE", "failed")
            startActivity(intent)
        }
    }

    private fun syncLocalTimeWithServerTime(order: Order) {
        val offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset")
        offsetRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
            .addOnFailureListener{e -> Toast.makeText(this, ""+e.message, Toast.LENGTH_SHORT).show()}
            .addOnCompleteListener{task ->
                //Clean cart
                if(task.isSuccessful)
                {
                    cartDataSource!!.cleanCart(Common.currentUser!!.uid!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object :SingleObserver<Int>{
                            override fun onSuccess(t: Int) {
                                Toast.makeText(this@MomoPayment, "Order placed successfully", Toast.LENGTH_SHORT).show()
                                EventBus.getDefault().postSticky(CountCartEvent(true))
                                compositeDisposable.dispose()
                            }

                            override fun onSubscribe(d: Disposable) {
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(this@MomoPayment, "" + e.message, Toast.LENGTH_SHORT).show()
                            }

                        })
                }

            }
    }

    override fun onLoadTimeSuccess(order: Order, estimateTimeMs: Long) {
        order.createDate = estimateTimeMs
        writeOrderToFireBase(order)
    }

    override fun onLoadTimeFailed(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }
}