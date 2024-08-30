package com.alexander.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.alexander.foodrunner.R

class SplashActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        getSupportActionBar()?.hide()

        Handler().postDelayed({
            val loginAct = Intent( this@SplashActivity, LoginActivity::class.java )
            startActivity(loginAct)
            this.finish()
        }, 1000)


    }
}

























