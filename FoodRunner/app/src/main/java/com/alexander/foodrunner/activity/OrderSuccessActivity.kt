package com.alexander.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.room.Room
import com.alexander.foodrunner.R
import com.alexander.foodrunner.database.MyCartDatabase

class OrderSuccessActivity : AppCompatActivity()
{
    lateinit var done : TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)

        done = findViewById(R.id.btnDone)

        done.setOnClickListener {
            val intent = Intent(this@OrderSuccessActivity, MainActivity::class.java)
            startActivity(intent)
            clearCart()
            this.finishAffinity()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this@OrderSuccessActivity, MainActivity::class.java)
        startActivity(intent)
        clearCart()
        this.finishAffinity()
    }

    private fun clearCart ()
    {
        val backend = BackendDeleteAllCart(applicationContext).execute().get()
    }

    private class BackendDeleteAllCart (val context: Context) : AsyncTask<Void, Void, Boolean>()
    {
        val db = Room.databaseBuilder(context, MyCartDatabase::class.java,"foods-db").build()

        override fun doInBackground(vararg params: Void?): Boolean
        {
            val myCartItems = db.foodDao().getAllFood()

            for(item in myCartItems)
                db.foodDao().delete(item)
            db.close()
            return true
        }
    }
}







