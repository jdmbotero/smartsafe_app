package com.smartsafe.smartsafe_app.presentation.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smartsafe.smartsafe_app.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}