package com.example.weatherforecast

import android.app.Application
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        appBarConfiguration= AppBarConfiguration(
            setOf(R.id.homeFragment,R.id.favoriteFragment,R.id.alarmFragment),findViewById<DrawerLayout>(R.id.drawerLayout))
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController
        setSupportActionBar(binding.mainToolbar)
        setupActionBarWithNavController(navController,appBarConfiguration)
        binding.navBar.setupWithNavController(navController)
        binding.sideDrawer.setupWithNavController(navController)
        //binding.gifBG.setBackgroundResource(R.drawable.morning_bg)



    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)||super.onSupportNavigateUp()
    }
}