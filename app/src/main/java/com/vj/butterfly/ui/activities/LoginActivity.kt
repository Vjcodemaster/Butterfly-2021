package com.vj.butterfly.ui.activities

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.snackbar.Snackbar
import com.vj.butterfly.R
import com.vj.butterfly.app_utility.*
import com.vj.butterfly.databinding.ActivityLoginBinding
import com.vj.butterfly.ui.base.BaseActivity
import com.vj.butterfly.viewmodels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class LoginActivity : BaseActivity() {
    private lateinit var mActivityLoginBinding: ActivityLoginBinding
    private lateinit var mLoginViewModel: LoginViewModel

    private var intentFilter: IntentFilter? = null
    private var otpReceiver: OTPReceiver? = null
    //private var otpVerifier : OTPVerifier = OTPVerifier()

    companion object {
        lateinit var permissionHandler: PermissionHandler
    }

    /*override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_login)
        mActivityLoginBinding = DataBindingUtil.setContentView(
            this@LoginActivity,
            R.layout.activity_login
        )
        mLoginViewModel = ViewModelProvider(
            this,
        ).get(LoginViewModel::class.java)
        mActivityLoginBinding.loginVM = mLoginViewModel
        mActivityLoginBinding.lifecycleOwner = this@LoginActivity
        biometricAuthentication { isSuccess ->
            if(isSuccess){
                showSnackBarToast(
                    "Authentication Success", Snackbar.LENGTH_LONG, EnumConstants.POSITIVE_ACTION.ordinal
                )
            } else {
                finish()
            }
        }
        AppPreferences.init(this)
        checkLogin()

        mLoginViewModel.registerResources(resources)
        permissionHandler = PermissionHandler()
        //mLoginViewModel.getPhoneNumberOfUser()
        /*val appSignatureHashHelper = AppSignatureHashHelper(this)
        appSignatureHashHelper.appSignatures*/
        registerObservers()
        initBroadCast()
        initSmsListener()
    }

    private fun checkLogin(){
        if(AppPreferences.loggedIn) {
            mActivityLoginBinding.etPhoneNumber.visibility = View.GONE
            mActivityLoginBinding.mBtnLogin.visibility = View.GONE

            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

            layoutParams.bottomToBottom = ConstraintSet.PARENT_ID
            layoutParams.endToEnd = ConstraintSet.PARENT_ID
            layoutParams.startToStart = ConstraintSet.PARENT_ID
            layoutParams.topToTop = ConstraintSet.PARENT_ID

            mActivityLoginBinding.lottieView.layoutParams = layoutParams

            CoroutineScope(Dispatchers.Main).launch {
                delay(1500)
                openMainActivity()
            }
            return
        }
    }

    private fun openMainActivity(){
        val mainActivityIntent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
    }

    private fun initBroadCast() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        otpReceiver = OTPReceiver()
        otpReceiver?.setOTPListener(object : OTPReceiver.OTPReceiveListener {
            override fun onOTPReceived(otp: String?) {
                //showToast("OTP Received: $otp")
                if (otp == mLoginViewModel.otp) {
                    AppPreferences.loggedIn = true
                   openMainActivity()
                }
                //Toast.makeText(this@LoginActivity, otp, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initSmsListener() {
        val client = SmsRetriever.getClient(this)
        client.startSmsRetriever()
    }

    override fun onStart() {
        super.onStart()
        //permissionHandler.requestSMSPermission(this)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(otpReceiver)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(otpReceiver, intentFilter)
    }

    private fun registerObservers() {
        //mActivityLoginBinding.mBtnLogin.setBackgroundT
        mLoginViewModel.phoneNumber.observe(this, { number ->
            if (number.length == 10) {
                mLoginViewModel.setLoginButtonEnabled(true)
                ContextCompat.getColorStateList(this, R.color.pink_red_dark)?.let {
                    mLoginViewModel.setLoginButtonBackgroundTintColor(
                        it
                    )
                }

            } else {
                mLoginViewModel.setLoginButtonEnabled(false)
                ContextCompat.getColorStateList(this, R.color.blue_dark)?.let {
                    mLoginViewModel.setLoginButtonBackgroundTintColor(
                        it
                    )
                }
            }
        })

        mLoginViewModel.askSMSPermission.observe(this, {
            if (it)
                permissionHandler.requestSMSPermission(this)
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == EnumConstants.SMS_PERMISSION.ordinal) {
            //var granted = false
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    permissionHandler.permissionSendSMS
                )
            ) {
                //granted = false
                toastNeedPermission()
            } else {
                if (permissionHandler.permissionGranted(
                        permissionHandler.permissionSendSMS,
                        this
                    )
                ) {
                    //granted = true
                    //mLoginViewModel.generateRandomOTPAndSendSMS(resources.getString(R.string.message))
                    mLoginViewModel.generateRandomOTPAndSendSMS("<#> Your Butterfly code is: ")
                } else {
                    //granted = false
                    toastNeedPermission()
                }
            }
        }
    }


    private fun toastNeedPermission() {
        Toast.makeText(
            this,
            "SMS permission required to login Sweetheart <3...",
            Toast.LENGTH_SHORT
        ).show()
        this.finish()
    }

    /*private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            when (requestCode) {
                101 -> {

                }
            }
        }
    }*/
    /*fun encryptPhoneNumber(){
        val bytes: ByteArray = "phonenumber".toByteArray()
        val string = Base64.getEncoder().encodeToString(bytes)

        val bytes1 = Base64.getDecoder().decode(string)
        val string1 = String(bytes1)
    }*/
    /*fun onClick(view : View){
        when(view.id){

        }
    }*/

    /*private fun hideSystemBars() {
        val insetsControllerCompat = WindowInsetsControllerCompat(window, window.decorView)
        insetsControllerCompat.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsControllerCompat.hide(systemBars())
    }*/

    /*private fun showSystemBars() {
        val insetsControllerCompat = WindowInsetsControllerCompat(window, window.decorView)
        insetsControllerCompat.show(systemBars())
    }*/

    /*private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hideSystemBars()
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }*/
}