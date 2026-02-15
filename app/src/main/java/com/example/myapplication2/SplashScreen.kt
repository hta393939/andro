package com.example.myapplication2

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle

@SuppressLint("CustomSplashScreen")
class SplashScreen: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0
            )
        } else {

            val intent = Intent(application, SelectDeviceActivity::class.java)
            startActivity(intent)
            //startActivity<SplashScreen>()

            finish()
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(application, SelectDeviceActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

}