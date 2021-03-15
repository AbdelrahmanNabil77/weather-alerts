package com.example.weatherforecast.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
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
        checkDrawOverAppsPermissionsDialog()
        runBackgroundPermissions()
        if(isFirstTime()){
            if(!Utility.isOnline(this)){
                noInternetDialog()
            }
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
            if (!sharedPref.contains(Constants.sharedPrefFirstTime)) {
                if(Utility.isOnline(this)){
                editor.putBoolean(Constants.sharedPrefFirstTime, true).apply()
                return true
                }else{
                    noInternetDialog()
                    return false
                }
            } else {
                return false
            }
        }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun checkDrawOverAppsPermissionsDialog() {
        if (!Settings.canDrawOverlays(this)) {
        AlertDialog.Builder(this).setTitle("Permission request").setCancelable(false).setMessage("Allow Draw Over Apps Permission to be able to use application probably")
            .setPositiveButton("Yes") { dialog, which -> drawOverAppPermission() }.setNegativeButton(
                "No"
            ) { dialog, which -> errorWarningForNotGivingDrawOverAppsPermissions() }.show()}
    }

    fun drawOverAppPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 80)
            }
        }
    }


    private fun errorWarningForNotGivingDrawOverAppsPermissions() {
        AlertDialog.Builder(this).setTitle("Warning").setCancelable(false).setMessage(
            """
    Unfortunately the display over other apps permission is not granted so the application might not behave properly 
    To enable this permission kindly restart the application
    """.trimIndent()
        )
            .setPositiveButton("Ok") { dialog, which -> }.show()
    }

    fun runBackgroundPermissions() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            if (Build.BRAND.equals("xiaomi", ignoreCase = true)) {
                goToXiaomiPermissions(this)
                val intent = Intent()
                intent.component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
                startActivity(intent)
            } else if (Build.BRAND.equals("Honor", ignoreCase = true) || Build.BRAND.equals(
                    "HUAWEI",
                    ignoreCase = true
                )) {
                val intent = Intent()
                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
                startActivity(intent)
            }
        }
    }

    private fun noInternetDialog() {
        AlertDialog.Builder(this).setTitle("Network Error").setMessage("Ooops!!\nThe application needs to be connected" +
                " to the internet at least for the first time.\n" +
                "May you switch on the internet connection and restart the application?").setPositiveButton("Restart") { dialog, which ->
            val intent =intent
            finish()
            startActivity(intent)
        }.setNegativeButton("Exit") { dialog, which -> finish() }.show()
    }

    private fun setAppLocale(localeCode: String){
        val resources = resources;
        val dm = resources.getDisplayMetrics()
        val config = resources.getConfiguration()
        config.setLocale(Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(config, dm);
    }

    fun isMiUi(): Boolean {
        return getSystemProperty("ro.miui.ui.version.name")?.isNotBlank() == true
    }

    fun isMiuiWithApi28OrMore(): Boolean {
        return isMiUi() && Build.VERSION.SDK_INT >= 28
    }

    fun goToXiaomiPermissions(context: Context) {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
        intent.putExtra("extra_pkgname", context.packageName)
        context.startActivity(intent)
    }

    private fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return line
    }
}