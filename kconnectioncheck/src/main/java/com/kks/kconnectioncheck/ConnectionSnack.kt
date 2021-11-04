package com.kks.kconnectioncheck

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.yammobots.kconnectioncheck.R

object ConnectionSnack {
    fun show(
        context: Context?,
        parent: View,
        isConnectionAvailable: Boolean,
        connectionBuilder: KConnectionCheck.CustomConnectionBuilder?
    ) {
        val inflater = LayoutInflater.from(context)

        // Create the Snackbar
        val snackbar: Snackbar = if (isConnectionAvailable) {
            if (connectionBuilder != null && !connectionBuilder.isHideWhenConnectionRestored) Snackbar.make(
                parent,
                "",
                Snackbar.LENGTH_INDEFINITE
            ) else Snackbar.make(parent, "", Snackbar.LENGTH_SHORT)
        } else Snackbar.make(parent, "", Snackbar.LENGTH_INDEFINITE)

        // Get the Snackbar's layout view
        val snackLayout = snackbar.view as SnackbarLayout
        snackLayout.setBackgroundColor(Color.TRANSPARENT)

        // Hide the text
        val textView =
            snackLayout.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        textView.visibility = View.INVISIBLE

        // Inflate and prepare custom Layout
        val customLayout: View = inflater.inflate(R.layout.abutil_connection_layout, null)
        val connectionTextView = customLayout.findViewById<TextView>(R.id.tv_connection)
        val connectionImageView = customLayout.findViewById<ImageView>(R.id.iv_connection)
        val dismissTextView = customLayout.findViewById<TextView>(R.id.tv_connection_dismiss)
        dismissTextView.setOnClickListener { snackbar.dismiss() }
        if (isConnectionAvailable) {
            if (connectionBuilder != null && connectionBuilder.connectionRestoredDrawable != 0) connectionImageView.setImageResource(
                connectionBuilder.connectionRestoredDrawable
            ) else connectionImageView.setImageResource(R.drawable.ic_connection_restored)
            if (connectionBuilder?.connectionRestoredText != null) connectionTextView.setText(
                connectionBuilder.connectionRestoredText
            ) else connectionTextView.setText(R.string.connection_restored)
            if (connectionBuilder != null && connectionBuilder.connectionRestoredTextColor != 0) connectionTextView.setTextColor(
                connectionBuilder.connectionRestoredTextColor
            )
        } else {
            if (connectionBuilder != null && connectionBuilder.noConnectionDrawable != 0) connectionImageView.setImageResource(
                connectionBuilder.noConnectionDrawable
            ) else connectionImageView.setImageResource(R.drawable.ic_no_connection)
            if (connectionBuilder?.noConnectionText != null) connectionTextView.text =
                connectionBuilder.noConnectionText else connectionTextView.setText(R.string.no_connection)
            if (connectionBuilder != null && connectionBuilder.noConnectionTextColor != 0) connectionTextView.setTextColor(
                connectionBuilder.noConnectionTextColor
            )
        }
        if (connectionBuilder != null && connectionBuilder.dismissTextColor != 0) dismissTextView.setTextColor(
            connectionBuilder.dismissTextColor
        )
        if (connectionBuilder?.dismissText != null) dismissTextView.text = connectionBuilder.dismissText

        //If the view is not covering the whole snackbar layout, add this line
        snackLayout.setPadding(0, 0, 0, 0)

        // Add the view to the Snackbar's layout
        snackLayout.addView(customLayout, 0)
        if (connectionBuilder?.bottomNavigationView != null && snackbar.view.layoutParams is CoordinatorLayout.LayoutParams
            && parent is BottomNavigationView
        ) {
            val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
            params.anchorId = parent.getId()
            params.anchorGravity = Gravity.TOP
            params.gravity = Gravity.TOP
            snackbar.view.layoutParams = params
        } else if (connectionBuilder?.bottomNavigationView != null && snackbar.view.layoutParams is CoordinatorLayout.LayoutParams) {
            val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
            params.anchorGravity = Gravity.BOTTOM
            params.gravity = Gravity.BOTTOM
            snackbar.view.layoutParams = params
        }

        // Show the Snackbar
        snackbar.show()
    }
}