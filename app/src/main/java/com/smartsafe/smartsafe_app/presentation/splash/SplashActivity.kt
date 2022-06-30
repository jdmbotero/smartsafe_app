package com.smartsafe.smartsafe_app.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smartsafe.smartsafe_app.databinding.ActivitySplashBinding
import com.smartsafe.smartsafe_app.presentation.auth.AuthActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}