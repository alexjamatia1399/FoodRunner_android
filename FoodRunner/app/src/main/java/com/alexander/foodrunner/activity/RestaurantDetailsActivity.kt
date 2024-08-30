package com.alexander.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.alexander.foodrunner.R
import com.alexander.foodrunner.adapter.RestaurantRecyclerAdapter
import com.alexander.foodrunner.database.FoodEntity
import com.alexander.foodrunner.database.MyCartDatabase
import com.alexander.foodrunner.fragment.HomeFragment
import com.alexander.foodrunner.model.Food
import com.alexander.foodrunner.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import java.util.ArrayList

class RestaurantDetailsActivity : AppCompatActivity()
{
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter : RestaurantRecyclerAdapter
    lateinit var recyclerLayoutManager : RecyclerView.LayoutManager
    lateinit var btnCart : android.widget.Button
    lateinit var loading : RelativeLayout
    lateinit var toolbar: Toolbar
    lateinit var database : MyCartDatabase
    var foodList = arrayListOf<Food>()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        recyclerView = findViewById(R.id.recyclerResDetails)
        btnCart = findViewById(R.id.btnCart)
        loading = findViewById(R.id.loadingLayoutRD)
        toolbar = findViewById(R.id.resDetailsToolbar)
        recyclerLayoutManager = LinearLayoutManager(this@RestaurantDetailsActivity)


        var resId : String? = null
        var resName : String? = null
        if(intent!=null)
        {
            resId = intent.getStringExtra("ResId")
            resName = intent.getStringExtra("ResName")
        }

        setupToolbar(resName)
        recyclerView.layoutManager = recyclerLayoutManager
        loading.visibility = View.VISIBLE
        btnCart.visibility = View.GONE



        val queueOfRequests = Volley.newRequestQueue(this@RestaurantDetailsActivity)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$resId"

        val jsonObjectRequest = object : JsonObjectRequest(Method.GET,url,null,

                Response.Listener{

                    loading.visibility = View.GONE

                    try{
                        val maindata = it.getJSONObject("data")
                        val success = maindata.getBoolean("success")
                        val data = maindata.getJSONArray("data")


                        if(success)
                        {
                            for(x in 0 until data.length())
                            {
                                val obj = data.getJSONObject(x)
                                val foodItem = Food ( obj.getString("id"),
                                        obj.getString("name"),
                                        obj.getString("cost_for_one"),
                                        obj.getString("restaurant_id")
                                )
                                foodList.add(foodItem)
                            }

                            recyclerAdapter = RestaurantRecyclerAdapter(
                                    this@RestaurantDetailsActivity, foodList )
                            recyclerView.adapter = recyclerAdapter
                        }



                        else
                        {
                            val box = AlertDialog.Builder(this@RestaurantDetailsActivity)
                            box.setTitle("Failed")
                            box.setMessage("success:false | Error in JsonObjectRequest")
                            box.setPositiveButton("Okay") {text, Listener->}
                            box.create()
                            box.show()
                        }

                    }catch (e:JSONException) {
                        val box = AlertDialog.Builder(this@RestaurantDetailsActivity)
                        box.setTitle("JsonError")
                        box.setMessage("Some JsonException Error has occurred!!!!")
                        box.setPositiveButton("Okay") {text, Listener->}
                        box.create()
                        box.show()
                    }
                },

                Response.ErrorListener{
                    val box = AlertDialog.Builder(this@RestaurantDetailsActivity)
                    box.setTitle("VolleyError")
                    box.setMessage("Some Error has occurred in Volley Requests!!!!")
                    box.setPositiveButton("Okay") {text, Listener->}
                    box.create()
                    box.show()
                })
        {
            override fun getHeaders(): MutableMap<String, String>
            {
                val headers = HashMap< String,String >()
                headers["Content-type"] = "application/json"
                headers["token"] = "fb3de6b5669b46"
                return headers
            }
        }


        val online = ConnectionManager().checkConnectivity(this@RestaurantDetailsActivity)

        if(online)
        {
            queueOfRequests.add(jsonObjectRequest)

            btnCart.setOnClickListener {
                val intent = Intent(this@RestaurantDetailsActivity,MyCartActivity::class.java)
                intent.putExtra("RestaurantId",resId)
                intent.putExtra("RestaurantName",resName)
                startActivity(intent)
            }

        }
        else
        {
            val box = AlertDialog.Builder(this@RestaurantDetailsActivity)
            box.setTitle("Offline")
            box.setMessage("No Internet Connection is found!!!!")
            box.setPositiveButton("Open Settings") {text, Listener->
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
            }
            box.setNegativeButton("Close App") {text, Listener->
                ActivityCompat.finishAffinity(this@RestaurantDetailsActivity)
            }
            box.create()
            box.show()
        }


    }


    private fun setupToolbar(restaurant_name:String?)
    {
        setSupportActionBar(toolbar)
        if(restaurant_name != null)
            supportActionBar?.title = restaurant_name
        else
            supportActionBar?.title = "NoRestaurantName"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun activateCartButton() { btnCart.visibility = View.VISIBLE }
    fun deactivateCartButton() { btnCart.visibility = View.GONE }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val id = item.itemId
        if(id == android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        clearCart()
    }

    private fun clearCart ()
    {
        val backend = BackendDeleteAllCart(applicationContext).execute().get()
    }

    private class BackendDeleteAllCart (context:Context) : AsyncTask<Void, Void, Boolean>()
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