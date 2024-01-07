package com.alamin1x0.e_oms.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import com.alamin1x0.e_oms.R
import com.alamin1x0.e_oms.databinding.ActivitySplashScreenBinding
import kotlin.math.log

class SplashScreen : AppCompatActivity() {


    var preferences: SharedPreferences? = null

    lateinit var binding:ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val pInfo = packageManager.getPackageInfo(packageName, 0)
        val version = pInfo.versionName //Version Name
        binding.versionName.setText(version)

        preferences = this.getSharedPreferences("users", AppCompatActivity.MODE_PRIVATE)
        val name = preferences!!.getString("userName", "")!!

        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            if (name.isEmpty()) {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            } else {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }, 1000)
    }
}
