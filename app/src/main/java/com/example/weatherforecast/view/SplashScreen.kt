package com.example.weatherforecast.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    lateinit var binding:ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.weatherLogo.alpha=0f
        binding.weatherLogo.animate().setDuration(3000).alpha(1f).withEndAction {
         intent= Intent(this,MainActivity::class.java)
         startActivity(intent)
         overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
         finish()
        }
    }
}