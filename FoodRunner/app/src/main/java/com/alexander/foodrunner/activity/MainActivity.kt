package com.alexander.foodrunner.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.alexander.foodrunner.R
import com.alexander.foodrunner.database.FoodEntity
import com.alexander.foodrunner.database.MyCartDatabase
import com.alexander.foodrunner.database.RestaurantDatabase
import com.alexander.foodrunner.database.RestaurantEntity
import com.alexander.foodrunner.fragment.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity()
{
    lateinit var drawer: DrawerLayout
    lateinit var toolbar : androidx.appcompat.widget.Toolbar
    lateinit var navigator : NavigationView
    lateinit var coordinator : CoordinatorLayout
    lateinit var frame : FrameLayout
    lateinit var loginfile : SharedPreferences
    lateinit var yourName : TextView
    lateinit var yourEmail : TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        loginfile = getSharedPreferences(getString(R.string.logindata),Context.MODE_PRIVATE)

        drawer = findViewById(R.id.drawerlayout)
        navigator = findViewById(R.id.navigator)
        toolbar = findViewById(R.id.toolbar)
        coordinator = findViewById(R.id.coordinatorlayout)
        frame = findViewById(R.id.framelayout)

        val header = navigator.getHeaderView(0)
        yourName = header.findViewById(R.id.yourname)
        yourEmail = header.findViewById(R.id.youremail)

        yourName.text = loginfile.getString("Name","alexander")
        yourEmail.text = loginfile.getString("Email","abc@gmail.com")

        setupToolbar()
        setupHome()
        clearCart()


        val actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity,drawer,
                R.string.open_drawer, R.string.close_drawer)
        drawer.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()


        var prevItem : MenuItem? = null

        navigator.setNavigationItemSelectedListener {

            if(prevItem != null)
                prevItem?.isChecked=false
            it.isCheckable = true
            it.isChecked = true
            prevItem = it

            when(it.itemId)
            {
                R.id.homeMenu -> {
                    setupHome()
                    drawer.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, ProfileFragment())
                            .commit()
                    supportActionBar?.title= "My Profile"
                    drawer.closeDrawers()
                }
                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.framelayout, FavouritesFragment())
                            .commit()
                    supportActionBar?.title= "Favourite Restaurants"
                    drawer.closeDrawers()
                }
                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, OrderHistoryFragment())
                        .commit()
                    supportActionBar?.title= "My Previous Orders"
                    drawer.closeDrawers()
                }
                R.id.faq -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, FaqFragment())
                            .commit()
                    supportActionBar?.title= "Frequently Asked Questions"
                    drawer.closeDrawers()
                }
                R.id.logout -> {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to logout ?")
                    dialog.setPositiveButton("Yes") {text, Listener ->
                        loginfile.edit().putBoolean("LoggedIn",false).apply()
                        loginfile.edit().clear().apply()
                        startActivity(intent)
                        this.finishAffinity()
                    }
                    dialog.setNegativeButton("No") {text, Listener -> }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val id = item.itemId
        if(id==android.R.id.home)
            drawer.openDrawer(GravityCompat.START)

        return super.onOptionsItemSelected(item)
    }

    fun setupToolbar ()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title= getString(R.string.app_name)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun setupHome()
    {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.framelayout, HomeFragment())
        transaction.commit()
        supportActionBar?.title= "All Restaurants"
        navigator.setCheckedItem(R.id.homeMenu)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.framelayout)

        if (frag !is HomeFragment)
            setupHome()
        else
            super.onBackPressed()

    }

    private fun clearCart ()
    {
        val backend = BackendDeleteAllCart(applicationContext).execute().get()
    }

    private class BackendDeleteAllCart (val context:Context) : AsyncTask<Void, Void, Boolean>()
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



















