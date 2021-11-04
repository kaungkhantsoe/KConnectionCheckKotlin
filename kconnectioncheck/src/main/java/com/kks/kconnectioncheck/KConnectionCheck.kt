package com.kks.kconnectioncheck

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import com.yammobots.kconnectioncheck.R
import java.lang.Exception

object KConnectionCheck {
    private var connectionStatus = true
    fun addConnectionCheck(
        context: Context,
        lifecycleOwner: LifecycleOwner?,
        connectionStatusChangeListener: ConnectionStatusChangeListener
    ) {
        ConnectionLiveData(context).observe(
            lifecycleOwner!!
        ) { isConnected ->
            if (connectionStatus != isConnected) {
                connectionStatus = isConnected ?: false
                connectionStatusChangeListener.onConnectionStatusChange(isConnected ?: false)
                try {
                    ConnectionSnack.show(
                        context,
                        (context as Activity).findViewById(android.R.id.content),
                        isConnected ?: false,
                        null
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun addConnectionCheck(
        context: Context,
        lifecycleOwner: LifecycleOwner?,
        connectionStatusChangeListener: ConnectionStatusChangeListener,
        customConnectionBuilder: CustomConnectionBuilder?
    ) {
        ConnectionLiveData(context).observe(
            lifecycleOwner!!
        ) { isConnected ->
            if (connectionStatus != isConnected) {
                connectionStatus = isConnected ?: false
                connectionStatusChangeListener.onConnectionStatusChange(isConnected ?: false)
                if (customConnectionBuilder != null && customConnectionBuilder.isShowSnackOnStatusChange) if (customConnectionBuilder.bottomNavigationView != null) ConnectionSnack.show(
                    context,
                    customConnectionBuilder.bottomNavigationView!!,
                    isConnected ?: false,
                    customConnectionBuilder
                ) else ConnectionSnack.show(
                    context, (context as Activity).findViewById(
                        R.id.content
                    ), isConnected ?: false, customConnectionBuilder
                )
            }
        }
    }

    interface ConnectionStatusChangeListener {
        fun onConnectionStatusChange(status: Boolean)
    }

    class CustomConnectionBuilder(
        noConnectionText: String? = null,
        connectionRestoredText: String? = null,
        @DrawableRes noConnectionDrawable: Int? = 0,
        @DrawableRes connectionRestoredDrawable: Int? = 0,
        hideWhenConnectionRestored: Boolean? = true,
        @ColorInt noConnectionTextColor: Int? = 0,
        @ColorInt connectionRestoredTextColor: Int? = 0,
        @ColorInt dismissTextColor: Int? = 0,
        dismissText: String? = null,
        showSnackOnStatusChange: Boolean? = true,
        bottomNavigationView: View? = null
    ) {
        var noConnectionText: String? = null
        var connectionRestoredText: String? = null
        var noConnectionDrawable = 0
        var connectionRestoredDrawable = 0
        var isHideWhenConnectionRestored = true
        var noConnectionTextColor = 0
        var connectionRestoredTextColor = 0
        var dismissTextColor = 0
        var dismissText: String? = null
        var isShowSnackOnStatusChange = true
        var bottomNavigationView: View? = null

        init {
            this.noConnectionText = noConnectionText
            this.connectionRestoredText = connectionRestoredText
            this.noConnectionDrawable = noConnectionDrawable ?: 0
            this.connectionRestoredDrawable = connectionRestoredDrawable ?: 0
            isHideWhenConnectionRestored = hideWhenConnectionRestored ?: true
            this.noConnectionTextColor = noConnectionTextColor  ?: 0
            this.connectionRestoredTextColor = connectionRestoredTextColor ?: 0
            this.dismissTextColor = dismissTextColor ?: 0
            this.dismissText = dismissText
            isShowSnackOnStatusChange = showSnackOnStatusChange ?: true
            this.bottomNavigationView = bottomNavigationView
        }
    }
}
