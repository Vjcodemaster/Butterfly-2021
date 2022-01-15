package com.vj.butterfly.ui.base

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.snackbar.Snackbar
import com.vj.butterfly.R
import com.vj.butterfly.app_utility.BiometricPromptUtils
import com.vj.butterfly.app_utility.EnumConstants
import com.vj.butterfly.app_utility.network.NetworkState

abstract class BaseActivity : AppCompatActivity() {
    //lateinit var networkState: NetworkState
    lateinit var snackBarParentView: View

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //biometricAuthentication()
        //setContentView(R.layout.activity_base)
    }

    fun biometricAuthentication(biometricValidation: (isSuccess : Boolean) -> Unit){
        val biometric = BiometricPromptUtils.createBiometricPrompt(this, biometricValidation)
        val biometricPrompt = BiometricPromptUtils.createPromptInfo(this)
        biometric.authenticate(biometricPrompt)
    }

    private fun hideSystemBars() {
        val insetsControllerCompat = WindowInsetsControllerCompat(window, window.decorView)
        insetsControllerCompat.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun hideSystemUI() {
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
    }


    fun showSnackBarToast(toast: String, duration: Int, reason: Int) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), toast, duration)
            /*.setAction("Action") {
                // Responds to click on the action
            }*/
        when(reason) {
            EnumConstants.NETWORK_DISCONNECTED.ordinal -> {
                snackBar.setAction("Dismiss") {
                    // Responds to click on the action
                    snackBar.dismiss()
                }
                snackBar.setBackgroundTint(ContextCompat.getColor(this, R.color.color_red))
                snackBar.setActionTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
            }
            EnumConstants.NETWORK_CONNECTED.ordinal, EnumConstants.POSITIVE_ACTION.ordinal -> {
                snackBar.setBackgroundTint(ContextCompat.getColor(this, R.color.color_green))
                snackBar.setActionTextColor(ResourcesCompat.getColor(resources, R.color.grey_light, null))
            }
        }
        //snackBar.setBackgroundTint(ResourcesCompat.getColor(resources, R.color.pink_red, null))
        //snackBar.setActionTextColor(ResourcesCompat.getColor(resources, R.color.white, null))

        snackBar.show()
    }

    fun autoHandleGenericActivityEvents(snackBarParentView: View, isNetworkStateMonitorRequired : Boolean){
        this.snackBarParentView = snackBarParentView

        if(isNetworkStateMonitorRequired)
        initNetworkState()
    }

    private fun initNetworkState() {
        //networkState = NetworkState()
        NetworkState.connectivityCallBack(this)

        observeNetworkChange()
    }

    private fun observeNetworkChange() {
        NetworkState.isNetworkAvailable.observe(this, {
            if (it) {
                showSnackBarToast(
                    resources.getString(R.string.network_connected),
                    Snackbar.LENGTH_SHORT,
                    EnumConstants.NETWORK_CONNECTED.ordinal
                )
            } else {
                showSnackBarToast(
                    resources.getString(R.string.network_disconnected),
                    Snackbar.LENGTH_INDEFINITE,
                    EnumConstants.NETWORK_DISCONNECTED.ordinal
                )
            }
        })
    }

    override fun onStart() {
        super.onStart()
        initNetworkState()
    }

    override fun onStop() {
        super.onStop()
        NetworkState.unRegisterNetworkCallBack()
    }

}