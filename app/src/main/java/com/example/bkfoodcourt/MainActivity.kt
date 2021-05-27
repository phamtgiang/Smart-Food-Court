package com.example.bkfoodcourt

//import com.google.android.gms.tasks.OnCompleteListener
//import com.google.firebase.auth.AuthResult
//import com.google.firebase.auth.FirebaseAuth

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bkfoodcourt.common.Common
import com.example.bkfoodcourt.common.CommonUtils
import com.example.bkfoodcourt.model.User
import com.example.bkfoodcourt.model.UserModel
import com.example.bkfoodcourt.model.OwnerModel
import com.example.bkfoodcourt.ui.home.CookHomeActivity
import com.example.bkfoodcourt.ui.home.ManagerHomeActivity
import com.example.bkfoodcourt.ui.home.OwnerHomeActivity
import com.example.bkfoodcourt.ui.home.StaffHomeActivity
import com.example.bkfoodcourt.ui.login.ForgotPasswordActivity
import com.example.bkfoodcourt.ui.login.RegisterActivity
import com.example.bkfoodcourt.ui.login.RegisterEmployeeActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class MainActivity : AppCompatActivity() {
    val RC_SIGN_IN = 1000
    var googleSignInClient : GoogleSignInClient? = null
    var callbackManager = CallbackManager.Factory.create()
    private var loading_Dialog : Dialog? = null
    private lateinit  var userRef:DatabaseReference
    private val database = Firebase.database
    private val myRef = database.getReference()

    var checkRelogin : Boolean = false

    override fun onResume() {
        super.onResume()
        val strLogOut = intent.getStringExtra("LOGGED_OUT")
        if (strLogOut == "true") {
            checkRelogin = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        google_login_button.setOnClickListener {
            if (!checkNetwork(this)) {
                Toast.makeText(this, "Please connect to network", Toast.LENGTH_SHORT).show() /** If the mobile is not connected to network, show an notification**/
            }
            else{
                googleSignInClient?.signOut()
                google_login_button.visibility = View.GONE
                var signInIntent = googleSignInClient?.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
                google_login_button.visibility = View.VISIBLE
            }
        }

        facebook_login_button.setOnClickListener {
            facebook_login_button.visibility = View.GONE
            facebookLogin()
            facebook_login_button.visibility = View.VISIBLE
        }

        /** Call function to check network connection on start up **/
//        if (!checkNetwork(this)) {
//            Toast.makeText(this, "Please connect to network", Toast.LENGTH_SHORT).show() /** If the mobile is not connected to network, show an notification**/
//        }

        button.setOnClickListener {
            button.visibility = View.GONE
            login()
            button.visibility = View.VISIBLE
        }

        textRegister.setOnClickListener{
            moveToRegister()
        }

        textForgotPassword.setOnClickListener {
            moveToForgotPassword()
        }

        text_register_employee.setOnClickListener {
            moveToRegisterEmployee()
        }
    }

    fun facebookLogin(){
        showLoading()
        if (!checkNetwork(this)) {
            Toast.makeText(this, "Please connect to network", Toast.LENGTH_SHORT).show() /** If the mobile is not connected to network, show an notification**/
            hideLoading()
            return
        }
        val accessToken : AccessToken? = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired()
        if (isLoggedIn == true){
//            hideLoading()
//            Toast.makeText(this, "is Login error", Toast.LENGTH_SHORT).show()
            LoginManager.getInstance().logOut()
        }
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                firebaseAuthWithFacebook(result)
            }

            override fun onCancel() {
                moveToLogin()
            }

            override fun onError(error: FacebookException?) {

            }
        })
    }

    var mAuth = FirebaseAuth.getInstance() /** Create an authentication instance **/
    /** Login function (activated when clicking login button) **/

    /******************************************************/
    fun login () {
        if (!checkNetwork(this)) {
            Toast.makeText(this, "Please connect to network", Toast.LENGTH_SHORT).show() /** If the mobile is not connected to network, show an notification**/
            return
        }
        val emailTxt = findViewById<EditText>(R.id.editTextTextEmailAddress) as EditText /** Get the content of the input box of email **/
        var email = emailTxt.text.toString() /** Convert to string **/
        val passwordTxt = findViewById<EditText>(R.id.editTextTextPassword) as EditText /** Get the content of the input box of password **/
        var password = passwordTxt.text.toString() /** Convert to string **/

        if (email.trim().isEmpty() && password.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.email_password_missing_error), Toast.LENGTH_SHORT).show()
        }
        else if (email.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.enter_email_error), Toast.LENGTH_SHORT).show()
        }
        else if (password.trim().isEmpty()){
            Toast.makeText(this, getString(R.string.password_register_error), Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, getString(R.string.invalid_email_error), Toast.LENGTH_SHORT).show()
        }
        else {
            showLoading()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener ( this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
//                    userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE)
//                    userRef.child(mAuth.currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener{
//                        override fun onDataChange(dataSnapshot:DataSnapshot) {
//                            // This method is called once with the initial value and again
//                            // whenever data at this location is updated.
//                            if(dataSnapshot.exists()) {
//                                val userModel = dataSnapshot.getValue(UserModel::class.java)
//                                Common.currentUser = userModel!!
//                                Toast.makeText(this@MainActivity, "Login Successfully", Toast.LENGTH_LONG).show() /** Show the notification that logged in successfully **/
//                                startActivity(Intent(this@MainActivity, HomeActivity::class.java)) /** START A NEW ACTIVITY (MOVE TO HOME PAGE) **/
//                                finish()
//                            }
//                        }
//
//                        override fun onCancelled(error : DatabaseError) {
//                            // Failed to read value
//                            Toast.makeText(this@MainActivity, ""+ error.message,Toast.LENGTH_SHORT).show()
//                        }
//                    })
                    moveToMainScreen()

                    /*var uid :String = mAuth.currentUser!!.uid
                    var name :String?
                    Common.currentUser!!.uid=uid*/
                  /*  Common.currentUser!!.name=name*/

                } else {
                    hideLoading()
                    Toast.makeText(this, "Wrong email/password", Toast.LENGTH_SHORT).show() /** Show the notification that logged failed **/
                }
            })

        }
    }

    /******************************************************/

    /** Check network function **/

    /******************************************************/

   fun checkNetwork (context: Context) : Boolean{
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        return isConnected
    }

    fun firebaseAuthWithFacebook(result: LoginResult?){
//        var credential = FacebookAuthProvider.getCredential(result?.accessToken?.token!!)
//        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
//            if (task.isSuccessful){
//                hideLoading()
//                Toast.makeText(this, "Facebook login success!", Toast.LENGTH_LONG).show()
//                moveToMainScreen()
//            }
//            else{
//                hideLoading()
//                Toast.makeText(this, "Facebook login fail!", Toast.LENGTH_LONG).show()
//            }
//        }
        var credential = FacebookAuthProvider.getCredential(result?.accessToken?.token!!)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful){
                var email : String? = FirebaseAuth.getInstance().currentUser!!.email
                var uid : String = FirebaseAuth.getInstance().currentUser!!.uid
                myRef.child(Common.USER_REFERENCE).child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (!p0.exists()){
                            val user = User("", "", email, uid, 0.0, "user", true)
                            myRef.child(Common.USER_REFERENCE).child(uid).setValue(user)
                        }
                        Common.myRef.child(Common.USER_REFERENCE).child(uid).addValueEventListener(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                var cur_user = p0.getValue(UserModel::class.java)
                                Common.currentUser = cur_user
                                hideLoading()
                                Toast.makeText(this@MainActivity, "Facebook login success!", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                                finish()
                            }
                        })
                    }
                })
            }
            else{
                hideLoading()
                Toast.makeText(this, "Facebook login fail!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.packageManager
                .getPackageInfo(pContext.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                println("printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
        } catch (e: Exception) {
        }
    }


    fun firebaseAuthWithGoogle(acct : GoogleSignInAccount?){
//        showLoading()
//        var credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
//        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
//                task ->
//            if (task.isSuccessful){
//                hideLoading()
//                Toast.makeText(this, "Google login success!", Toast.LENGTH_SHORT).show()
//                moveToMainScreen()
//            }
//            else{
//                hideLoading()
//                Toast.makeText(this, "Google login fail!", Toast.LENGTH_SHORT).show()
//            }
//        }
        showLoading()
        var credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                task ->
            if (task.isSuccessful){
                hideLoading()
                Toast.makeText(this, "Google login success!", Toast.LENGTH_SHORT).show()
                var email : String? = FirebaseAuth.getInstance().currentUser!!.email
                var uid : String = FirebaseAuth.getInstance().currentUser!!.uid
                myRef.child("Users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (!p0.exists()){
                            val user = User("", "", email, uid, 0.0, "user", true)
                            myRef.child("Users").child(uid).setValue(user)
                        }
                    }
                })
                moveToMainScreen()
            }
            else{
                hideLoading()
                Toast.makeText(this, "Google login fail!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            var task =  GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                var account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account);
            }
            else {
                moveToLogin()
            }
        }
    }

    fun moveToRegister(){
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun moveToLogin(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun moveToForgotPassword(){
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }

    private fun hideLoading(){
        loading_Dialog?.let {
            if (it.isShowing)
                it.cancel()
        }
    }

    private fun showLoading(){
        hideLoading()
        loading_Dialog = CommonUtils.showLoadingDialog(this)
    }

    private fun moveToRegisterEmployee(){
        startActivity(Intent(this, RegisterEmployeeActivity::class.java))
    }

    private fun moveToMainScreen(){
//        var uid :String = FirebaseAuth.getInstance().currentUser!!.uid
////        Common.currentUser!!.uid=uid
////        val userRef = myRef.child("Users").child(uid)
////        val ownerRef = myRef.child("Owners").child(uid)
////        val cookRef = myRef.child("Cooks").child(uid)
////        val managerRef = myRef.child("Manager").child(uid)
////        val staffRef = myRef.child("Staffs").child(uid)
////
////        checkTypeAndMove(userRef, "user")
////        checkTypeAndMove(ownerRef, "owner")
////        checkTypeAndMove(managerRef, "manager")
////        checkTypeAndMove(cookRef, "cook")
////        checkTypeAndMove(staffRef, "staff")

        /****/
        checkTypeAndMove(Common.USER_REFERENCE)
        checkTypeAndMove(Common.MANAGER_REFERENCE)
        checkTypeAndMove(Common.COOK_REFERENCE)
        checkTypeAndMove(Common.OWNER_REFERENCE)
        checkTypeAndMove(Common.STAFF_REFERENCE)
    }

    /****/
    private fun checkTypeAndMove(type : String){
        var uid :String = FirebaseAuth.getInstance().currentUser!!.uid
        myRef.child(type).child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    var uid :String = FirebaseAuth.getInstance().currentUser!!.uid
                    if (type == Common.USER_REFERENCE) {
//                        Common.currentUser!!.uid=uid
//                        val userModel = dataSnapshot.getValue(UserModel::class.java)
//                        Common.currentUser = userModel!!
                        myRef.child(type).child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val userModel = p0.getValue(UserModel::class.java)
                                Common.currentUser = userModel!!
                                hideLoading()
                                Toast.makeText(this@MainActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                                if (checkRelogin == true) {
                                    intent.putExtra("RELOGIN", "true")
                                }
                                //startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                                startActivity(intent)
                                finish()
                            }
                        })
                    }
                    else
                        loginIfIsActive(type)
                }
            }
        })
    }

    /**Check if account is active => move to home screen**/
    private fun loginIfIsActive(type : String){
        var uid :String = FirebaseAuth.getInstance().currentUser!!.uid
        myRef.child(type).child(uid).child(Common.IS_ACTIVE_REFERENCE).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
//                    Log.d("----------------DEBUG ACTIVE-----------", p0.getValue().toString())
                    if (p0.getValue() == true){
                        Common.currentUser!!.uid=uid
                        if (type == Common.MANAGER_REFERENCE)
                            startActivity(Intent(this@MainActivity, ManagerHomeActivity::class.java))
                        else if (type == Common.OWNER_REFERENCE){
                            myRef.child("Owners").child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    var cur_owner = p0.getValue(OwnerModel::class.java)
                                    Common.currentOwner = cur_owner
                                    startActivity(Intent(this@MainActivity, OwnerHomeActivity::class.java))
                                }
                            })
                        }
                        else if (type == Common.COOK_REFERENCE)
                            startActivity(Intent(this@MainActivity, CookHomeActivity::class.java))
                        else
                            startActivity(Intent(this@MainActivity, StaffHomeActivity::class.java))
                        hideLoading()
                        Toast.makeText(this@MainActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else{
                        hideLoading()
                        Toast.makeText(this@MainActivity, getString(R.string.account_not_active), Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
