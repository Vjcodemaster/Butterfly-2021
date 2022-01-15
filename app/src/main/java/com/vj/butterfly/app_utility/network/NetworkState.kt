package com.vj.butterfly.app_utility.network

import android.content.Context
import android.net.*
import android.os.Build
import androidx.lifecycle.MutableLiveData

object NetworkState {
    //var networkInfo: NetworkInfo? = null
    private var networkType: Int = NetworkCapabilities.TRANSPORT_CELLULAR
    //var initialNetworkType = 0
    //private var isFirstTimeDetected = false
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var connectivityManager: ConnectivityManager? = null
    var isNetworkAvailable: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    /*fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }*/

    /*fun getNetworkInfo(context: Context) {
        val cm: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkInfo = cm.activeNetworkInfo
    }*/

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw      = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                networkType = NetworkCapabilities.TRANSPORT_WIFI
                true
            }
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                networkType = NetworkCapabilities.TRANSPORT_CELLULAR
                true
            }
            //for other device which are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                networkType = NetworkCapabilities.TRANSPORT_ETHERNET
                true
            }
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) ->  {
                networkType = NetworkCapabilities.TRANSPORT_BLUETOOTH
                true
            }
            else -> false
        }
    }

    //To verify internet access:
    val isOnline: Boolean
        get() {
            try {
                val p1 = Runtime.getRuntime().exec("ping -c 1 www.google.com")
                val returnVal = p1.waitFor()
                return returnVal == 0
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    fun connectivityCallBack(context: Context) {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // network available
                isNetworkAvailable.postValue(true)
                try {
                    /*LiveSessionViewModel.Companion.getGenericMethod().invoke(
                        0,
                        EnumConstants.NETWORK_CONNECTED,
                        "Unable to connect server, please try again later",
                        true
                    )*/
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                /*getNetworkInfo(context)
                if (networkInfo?.type == ConnectivityManager.TYPE_WIFI) {
                    networkType = ConnectivityManager.TYPE_WIFI
                } else if (networkInfo?.type == ConnectivityManager.TYPE_MOBILE) {
                    networkType = ConnectivityManager.TYPE_MOBILE
                }
                if (!isFirstTimeDetected) {
                    initialNetworkType = networkType
                    isFirstTimeDetected = true
                }*/
            }

            override fun onLost(network: Network) {
                // network unavailable
                isNetworkAvailable.postValue(false)
                //getNetworkInfo(context)
            }
        }
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager?.registerDefaultNetworkCallback(networkCallback as ConnectivityManager.NetworkCallback)
        } else {
            val request: NetworkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
            connectivityManager?.registerNetworkCallback(request,
                networkCallback as ConnectivityManager.NetworkCallback
            )
        }
    }

    fun unRegisterNetworkCallBack() {
        try {
            networkCallback?.let {
                connectivityManager?.unregisterNetworkCallback(it) }
        } catch (exception: IllegalArgumentException) {
            exception.printStackTrace()
            //network is already unregistered
        }
    }
}