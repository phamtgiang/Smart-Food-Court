package com.example.bkfoodcourt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.database.CartDataSource
import com.example.bkfoodcourt.database.CartDatabase
import com.example.bkfoodcourt.database.LocalCartDataSource
import com.example.bkfoodcourt.eventbus.CategoryClick
import com.example.bkfoodcourt.eventbus.CountCartEvent
import com.example.bkfoodcourt.eventbus.FoodItemClick
import com.example.bkfoodcourt.eventbus.HideFABCart
import com.example.bkfoodcourt.model.CategoryModel
import com.example.bkfoodcourt.model.FoodModel
import com.example.bkfoodcourt.eventbus.BestDealItemClick
import com.example.bkfoodcourt.eventbus.PopularFoodItemClick
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import dmax.dialog.SpotsDialog
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var cartDataSource: CartDataSource
    private lateinit var navController:NavController
    private  var drawer: DrawerLayout?=null
    private var dialog:android.app.AlertDialog? = null
    private var update:Button ?=null
    override fun onResume() {
        super.onResume()
        countCartItem()

        val strMomo = intent.getStringExtra("MOMO_MESSAGE")
        if (strMomo == "success") {
            navController.navigate(R.id.nav_home)
        }
        else if (strMomo == "failed"){
            navController.navigate(R.id.nav_cart)
        }

        val strReLogin = intent.getStringExtra("RELOGIN")
        if (strReLogin == "true") {
            navController.navigate(R.id.nav_home)
        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



       dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()

        cartDataSource=LocalCartDataSource(CartDatabase.getInstance(this).cartDAO())
        fab.count = 0

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            navController.navigate(R.id.nav_cart)
        }
        drawer = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_menu, R.id.nav_food_detail,
                R.id.nav_cart, R.id.nav_view_order
            ), drawer
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        var headerView = navView.getHeaderView(0)
        var txt_user = headerView.findViewById<TextView>(R.id.txt_user)
        var txt_balance = headerView.findViewById<TextView>(R.id.txt_balance)
        update = headerView.findViewById<Button>(R.id.update_balance_button)

        Common.setSpanString("Hey, ",Common.currentUser!!.name, txt_user)
        Common.setSpanString("Balance: ",Common.formatPrice(Common.currentUser!!.balance) +" VND",txt_balance)
        update!!.setOnClickListener {
            Common.setSpanString("Balance: ",Common.formatPrice(Common.currentUser!!.balance) +" VND",txt_balance)
        }
        navView.setNavigationItemSelectedListener(object:NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                item.isChecked = true
                drawer!!.closeDrawers()
                if(item.itemId == R.id.nav_sign_out){
                    signOut()
                }
                else if(item.itemId == R.id.nav_home){
                    navController.navigate(R.id.nav_home)
                }
                else if(item.itemId == R.id.nav_cart){
                    navController.navigate((R.id.nav_cart))
                }
                else if(item.itemId == R.id.nav_menu){
                    navController.navigate(R.id.nav_menu)
                }
                else if(item.itemId == R.id.nav_view_order){
                    navController.navigate(R.id.nav_view_order)
                }

                return true
            }

        })
        countCartItem()


    }

    private fun signOut() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sign out")
            .setMessage("Do you want to exit?")
            .setNegativeButton("Cancel", { dialogInterface, _ -> dialogInterface.dismiss() })
            .setPositiveButton("OK") { dialogInterface, _ ->
                /*
                Common.foodSelected = null
                Common.categorySelected = null
                Common.currentUser = null

                 */
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()

                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("LOGGED_OUT", "true")
                startActivity(intent)
                finish()
            }
        val dialog = builder.create()
        dialog.show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
    @Subscribe(sticky = true, threadMode= ThreadMode.MAIN)
    fun onCategorySelected(event:CategoryClick){
        if (event.isSucess)
        {
            //Toast.makeText(this, "Click to "+ event.category.name, Toast.LENGTH_SHORT).show()
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_food_list)
        }
    }

    @Subscribe(sticky = true, threadMode= ThreadMode.MAIN)
    fun onHideFABEvent(event: HideFABCart){
        if (event.isHide)
        {
            fab.hide()
        }else{
            fab.show()
        }
    }

    @Subscribe(sticky = true, threadMode= ThreadMode.MAIN)
    fun onCountCartEvent(event: CountCartEvent){
        if (event.isSuccess)
        {
            countCartItem()
        }
    }

    @Subscribe(sticky = true, threadMode= ThreadMode.MAIN)
    fun onPopularFoodItemClick(event: PopularFoodItemClick){
        if (event.popularCategoryModel != null)
        {
            dialog!!.show()

            FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                .child(event.popularCategoryModel!!.menu_id!!)
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        dialog!!.dismiss()
                        Toast.makeText(this@HomeActivity,""+p0.message,Toast.LENGTH_SHORT).show()

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists())
                        {
                            Common.categorySelected = p0.getValue(CategoryModel::class.java)
                            Common.categorySelected!!.menu_id = p0.key
                            //Load Food
                            FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                                .child(event.popularCategoryModel!!.menu_id!!)
                                .child("foods")
                                .orderByChild("id")
                                .equalTo(event.popularCategoryModel.food_id)
                                .limitToLast(1)
                                .addListenerForSingleValueEvent(object:ValueEventListener{
                                    override fun onCancelled(p0: DatabaseError) {
                                        dialog!!.dismiss()
                                        Toast.makeText(this@HomeActivity,""+p0.message,Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        if(p0.exists())
                                        {
                                            for(foodSnapShot in p0.children)
                                            {
                                                Common.foodSelected = foodSnapShot.getValue(FoodModel::class.java)
                                                Common.foodSelected!!.key = foodSnapShot.key
                                            }
                                            navController!!.navigate(R.id.nav_food_detail)
                                        }
                                        else
                                        {

                                            Toast.makeText(this@HomeActivity,"Item doesn't exist",Toast.LENGTH_SHORT).show()
                                        }
                                        dialog!!.dismiss()
                                    }

                                })
                        }
                        else
                        {
                            dialog!!.dismiss()
                            Toast.makeText(this@HomeActivity,"Item doesn't exist",Toast.LENGTH_SHORT).show()
                        }
                    }

                })

        }
    }

    @Subscribe(sticky = true, threadMode= ThreadMode.MAIN)
    fun onBestDealItemClick(event: BestDealItemClick){
        if (event.bestDealModel != null)
        {
            dialog!!.show()

            FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                .child(event.bestDealModel!!.menu_id!!)
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        dialog!!.dismiss()
                        Toast.makeText(this@HomeActivity,""+p0.message,Toast.LENGTH_SHORT).show()

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists())
                        {
                            Common.categorySelected = p0.getValue(CategoryModel::class.java)
                            Common.categorySelected!!.menu_id = p0.key
                            //Load Food
                            FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                                .child(event.bestDealModel!!.menu_id!!)
                                .child("foods")
                                .orderByChild("id")
                                .equalTo(event.bestDealModel.food_id)
                                .limitToLast(1)
                                .addListenerForSingleValueEvent(object:ValueEventListener{
                                    override fun onCancelled(p0: DatabaseError) {
                                        dialog!!.dismiss()
                                        Toast.makeText(this@HomeActivity,""+p0.message,Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        if(p0.exists())
                                        {
                                            for(foodSnapShot in p0.children)
                                            {
                                                Common.foodSelected = foodSnapShot.getValue(FoodModel::class.java)
                                                Common.foodSelected!!.key = foodSnapShot.key
                                            }
                                            navController!!.navigate(R.id.nav_food_detail)
                                        }
                                        else
                                        {

                                            Toast.makeText(this@HomeActivity,"Item doesn't exist",Toast.LENGTH_SHORT).show()
                                        }
                                        dialog!!.dismiss()
                                    }

                                })
                        }
                        else
                        {
                            dialog!!.dismiss()
                            Toast.makeText(this@HomeActivity,"Item doesn't exist",Toast.LENGTH_SHORT).show()
                        }
                    }

                })

        }
    }



    @Subscribe(sticky = true, threadMode= ThreadMode.MAIN)
    fun onFoodSelected(event: FoodItemClick){
        if (event.isSuccess)
        {
            //Toast.makeText(this, "Click to "+ event.category.name, Toast.LENGTH_SHORT).show()
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_food_detail)
        }
    }

    private fun countCartItem(){
        cartDataSource.countItemCart(Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<Int>{
                override fun onSuccess(t: Int) {
                    fab.count=t
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    if(!e.message!!.contains("Query returned empty"))
                    Toast.makeText(this@HomeActivity, "[Count Cart]"+e.message, Toast.LENGTH_SHORT).show()
                    else
                        fab.count = 0
                }

            })
    }


}