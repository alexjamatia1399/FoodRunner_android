package com.alexander.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.alexander.foodrunner.R
import com.alexander.foodrunner.adapter.MyCartRecyclerAdapter
import com.alexander.foodrunner.database.FoodEntity
import com.alexander.foodrunner.database.MyCartDatabase
import com.alexander.foodrunner.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MyCartActivity : AppCompatActivity()
{
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: MyCartRecyclerAdapter
    lateinit var recyclerLayout: RecyclerView.LayoutManager
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var txtResName: TextView
    lateinit var btnOrder: android.widget.Button
    lateinit var nowUser : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)

        toolbar = findViewById(R.id.mycartToolbar)
        txtResName = findViewById(R.id.txtOrderFrom)
        btnOrder = findViewById(R.id.btnOrder)
        recyclerView = findViewById(R.id.recyclerMyCart)
        nowUser = getSharedPreferences(getString(R.string.logindata),Context.MODE_PRIVATE)
        recyclerLayout = LinearLayoutManager(this@MyCartActivity)

        val userId = nowUser.getString("Id","no_user_id")
        var resId : String? = null
        var resName : String? = null
        if(intent!=null) {
            resId = intent.getStringExtra("RestaurantId")
            resName = intent.getStringExtra("RestaurantName")
        }

        val orderList = foodsToBeOrdered()
        val foodItems = JSONArray()
        var totalCost = 0
        for (item in orderList) {
            totalCost += item.price.toInt()
            val obj = JSONObject()
            obj.put("food_item_id",item.food_id)
            foodItems.put(obj)
        }

        setupToolbar()
        txtResName.text = resName
        btnOrder.text = "PLACE ORDER( TOTAL RS.$totalCost )"
        recyclerAdapter = MyCartRecyclerAdapter( this@MyCartActivity, orderList )
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = recyclerLayout



        val queueOfRequests = Volley.newRequestQueue(this@MyCartActivity)

        val url = "http://13.235.250.119/v2/place_order/fetch_result/"

        val jsonParams = JSONObject()
        jsonParams.put("user_id",userId)
        jsonParams.put("restaurant_id",resId)
        jsonParams.put("total_cost",totalCost)
        jsonParams.put("food",foodItems)

        val jsonObjectRequest = object : JsonObjectRequest(Method.POST,url,jsonParams,

                Response.Listener{

                    try{
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if(success)
                        {
                            val intent = Intent(this@MyCartActivity,
                                OrderSuccessActivity::class.java)
                            startActivity(intent)
                            this.finish()
                        }
                        else
                        {
                            val box = AlertDialog.Builder(this@MyCartActivity)
                            box.setTitle("JsonRequest Failed")
                            box.setMessage("Order could not be placed")
                            box.setPositiveButton("Okay") {text, Listener->}
                            box.create()
                            box.show()
                        }

                    }catch (e: JSONException) {
                        val box = AlertDialog.Builder(this@MyCartActivity)
                        box.setTitle("JsonError")
                        box.setMessage("Some JsonException Error has occurred!!!!")
                        box.setPositiveButton("Okay") {text, Listener->}
                        box.create()
                        box.show()
                    }
                },

                Response.ErrorListener{
                    val box = AlertDialog.Builder(this@MyCartActivity)
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

        val online = ConnectionManager().checkConnectivity(this@MyCartActivity)

        if(online)
        {
            btnOrder.setOnClickListener {
                val box = AlertDialog.Builder(this@MyCartActivity)
                box.setTitle("Confirmation")
                box.setMessage("Are you sure you want to place the order ?")
                box.setPositiveButton("Yes") {text, Listener->
                    queueOfRequests.add(jsonObjectRequest)
                }
                box.setNegativeButton("No") {text, Listener-> }
                box.create()
                box.show()
            }
        }
        else
        {
            val box = AlertDialog.Builder(this@MyCartActivity)
            box.setTitle("Offline")
            box.setMessage("No Internet Connection is found!!!!")
            box.setPositiveButton("Open Settings") {text, Listener->
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
            }
            box.setNegativeButton("Close App") {text, Listener->
                ActivityCompat.finishAffinity(this@MyCartActivity)
            }
            box.create()
            box.show()
        }
    }


    private fun setupToolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "MyCart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun foodsToBeOrdered() : List<FoodEntity>
    {
        return BackendFoodsToOrder(applicationContext).execute().get()
    }

    private class BackendFoodsToOrder (val context: Context)
        : AsyncTask<Void, Void, List<FoodEntity>>()
    {
        val db = Room.databaseBuilder(context, MyCartDatabase::class.java,"foods-db").build()

        override fun doInBackground(vararg params: Void?): List<FoodEntity>
        {
            return db.foodDao().getAllFood()
        }
    }

    override fun onOptionsItemSelected(item:MenuItem):Boolean {
        if(item.itemId == android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

}




