package com.kks.kconnectioncheck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kks.kconnectioncheck.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), KConnectionCheck.ConnectionStatusChangeListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val builder = KConnectionCheck.CustomConnectionBuilder()
        builder.bottomNavigationView = binding.bottomNavigation

        KConnectionCheck.addConnectionCheck(
            this,
            this,
            this,
            builder
        )
    }

    override fun onConnectionStatusChange(status: Boolean) {

    }
}