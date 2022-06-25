package com.smartsafe.smartsafe_app.presentation.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smartsafe.smartsafe_app.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}