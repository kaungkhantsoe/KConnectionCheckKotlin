package com.kks.kconnectioncheck

import android.annotation.TargetApi
import android.content.Context
import android.net.*
import android.net.ConnectivityManager.NetworkCallback
import android.os.Build
import androidx.lifecycle.LiveData

class ConnectionLiveData(context: Context) : LiveData<Boolean?>() {
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var connectivityManagerCallback: NetworkCallback? = null
    private var networkRequestBuilder: NetworkRequest.Builder? = null
    override fun onActive() {
        super.onActive()
        updateConnection()
        networkRequestBuilder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                try {
                    connectivityMarshmallowManagerCallback?.let {
                        connectivityManager.registerDefaultNetworkCallback(it)
                    }
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                try {
                    marshmallowNetworkAvailableRequest()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
            else -> try {
                lollipopNetworkAvailableRequest()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(connectivityManagerCallback!!)
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
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                postValue(true)
            }

            override fun onLost(network: Network) {
                postValue(false)
            }
        }

    private val connectivityMarshmallowManagerCallback =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManagerCallback = object : NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        val hasTransport = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        postValue(hasTransport)
                    }
                }

                override fun onLost(network: Network) {
                    postValue(false)
                }
            }
            connectivityManagerCallback
        } else throw IllegalAccessException("Accessing wrong API version")

    private fun updateConnection() {
        val activeNetwork = connectivityManager.activeNetworkInfo
        if (activeNetwork != null)
            postValue(activeNetwork.isConnected)
        else
            postValue(false)
    }

}