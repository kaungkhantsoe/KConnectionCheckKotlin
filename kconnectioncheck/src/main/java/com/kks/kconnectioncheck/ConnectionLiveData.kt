package com.kks.kconnectioncheck

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.net.ConnectivityManager.NetworkCallback
import android.os.Build
import androidx.lifecycle.LiveData

class ConnectionLiveData(private val context: Context) : LiveData<Boolean?>() {
    private val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var connectivityManagerCallback: NetworkCallback? = null
    private var networkRequestBuilder: NetworkRequest.Builder? = null
    override fun onActive() {
        super.onActive()
        updateConnection()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkRequestBuilder = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    connectivityMarshmallowManagerCallback?.let {
                        connectivityManager.registerDefaultNetworkCallback(it)
                    }
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    marshmallowNetworkAvailableRequest()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    lollipopNetworkAvailableRequest()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        } else {
            context.registerReceiver(
                networkReceiver,
                IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
            ) // android.net.ConnectivityManager.CONNECTIVITY_ACTION
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback!!)
        } else {
            context.unregisterReceiver(networkReceiver)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Throws(IllegalAccessException::class)
    private fun lollipopNetworkAvailableRequest() {
        connectivityManager.registerNetworkCallback(
            networkRequestBuilder!!.build(),
            connectivityLollipopManagerCallback
        )
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Throws(IllegalAccessException::class)
    private fun marshmallowNetworkAvailableRequest() {
        connectivityMarshmallowManagerCallback?.let {
            connectivityManager.registerNetworkCallback(
                networkRequestBuilder!!.build(), it
            )
        }

    }

    private val connectivityLollipopManagerCallback =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    postValue(true)
                }

                override fun onLost(network: Network) {
                    postValue(false)
                }
            }
        } else throw IllegalAccessException("Accessing wrong API version")

    private val connectivityMarshmallowManagerCallback =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManagerCallback = object : NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) postValue(
                        true
                    )
                }

                override fun onLost(network: Network) {
                    postValue(false)
                }
            }
            connectivityManagerCallback
        } else throw IllegalAccessException("Accessing wrong API version")

    private val networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateConnection()
        }
    }

    private fun updateConnection() {
        val activeNetwork = connectivityManager.activeNetworkInfo
        if (activeNetwork != null) postValue(activeNetwork.isConnected) else postValue(false)
    }

}