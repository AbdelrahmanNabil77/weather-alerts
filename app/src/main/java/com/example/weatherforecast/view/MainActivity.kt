package com.example.weatherforecast.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.weatherforecast.NavGraphDirections
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.datalayer.Repository
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.utilities.Utility
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(Utility.getLocale(this))
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if(isFirstTime()){
            Toast.makeText(this,"welcome",Toast.LENGTH_LONG).show()
        }
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.favoriteFragment, R.id.alarmFragment)//,
            //findViewById<DrawerLayout>(R.id.drawerLayout)
        )
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController
        setSupportActionBar(binding.mainToolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navBar.setupWithNavController(navController)
        //binding.sideDrawer.setupWithNavController(navController)
        if (Utility.isMorning()) {
            binding.gifBG.setBackgroundResource(R.drawable.morning_bg_2)
        } else {
            binding.gifBG.setBackgroundResource(R.drawable.night_bg)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.popup_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(item.itemId==R.id.settingsFragment){
            val action=NavGraphDirections.actionGlobalSettingsFragment()
            navController.navigate(action)
            true
        }else{
            item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
        }
    }


    private fun isFirstTime():Boolean{
        val sharedPref = getSharedPreferences(Constants.sharedPrefSettings, MODE_PRIVATE)
        val editor = sharedPref.edit()
        return if(!sharedPref.contains(Constants.sharedPrefFirstTime)){
            editor.putBoolean(Constants.sharedPrefFirstTime,true).apply()
            true
        }else{
            false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setAppLocale(localeCode: String){
        val resources = resources;
        val dm = resources.getDisplayMetrics()
        val config = resources.getConfiguration()
        config.setLocale(Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(config, dm);
    }
}