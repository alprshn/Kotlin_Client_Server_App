package com.example.client_server_app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.client_server_app.R
import com.example.client_server_app.databinding.ActivityMainBinding
import com.example.client_server_app.utilities.PreferenceManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}