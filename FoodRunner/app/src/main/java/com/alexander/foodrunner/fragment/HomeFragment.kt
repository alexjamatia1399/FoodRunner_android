package com.alexander.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.alexander.foodrunner.R
import com.alexander.foodrunner.adapter.HomeRecyclerAdapter
import com.alexander.foodrunner.database.FoodEntity
import com.alexander.foodrunner.database.MyCartDatabase
import com.alexander.foodrunner.database.RestaurantDatabase
import com.alexander.foodrunner.database.RestaurantEntity
import com.alexander.foodrunner.model.Restaurant
import com.alexander.foodrunner.model.RestaurantHolder
import com.alexander.foodrunner.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerHome : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter : HomeRecyclerAdapter
    lateinit var loadingLayout : RelativeLayout
    val restaurantList = arrayListOf<RestaurantHolder>()

    var alphaComp = Comparator<RestaurantHolder> { resA, resB ->
         resA.res.name.compareTo(resB.res.name, true)
     }
    var ratingComp = Comparator<RestaurantHolder> { resA, resB ->
        resA.res.rating.compareTo(resB.res.rating, true)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val homeView = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerHome = homeView.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        loadingLayout = homeView.findViewById(R.id.loadingLayout)
        loadingLayout.visibility = View.VISIBLE


        val queueOfRequests = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        val jsonObjReq = object : JsonObjectRequest( Method.GET, url, null,

                Response.Listener{
                    try {
                        loadingLayout.visibility = View.GONE

                        val maindata = it.getJSONObject("data")
                        val success = maindata.getBoolean("success")

                        if (success)
                        {
                            val data = maindata.getJSONArray("data")
                            for (x in 0 until data.length())
                            {
                                val obj = data.getJSONObject(x)
                                val res = Restaurant(
                                        obj.getString("id"),
                                        obj.getString("name"),
                                        obj.getString("rating"),
                                        obj.getString("cost_for_one"),
                                        obj.getString("image_url")
                                )

                                val colorFav : Int
                                val checkFav = BackendIsResPresent(activity as Context, res).execute()
                                val isFav = checkFav.get()

                                if(isFav)
                                    colorFav = R.drawable.added_fav
                                else
                                    colorFav = R.drawable.not_added_fav

                                val resHolder = RestaurantHolder(res,colorFav)

                                restaurantList.add(resHolder)

                                restaurantList.shuffle()
                            }

                            recyclerAdapter = HomeRecyclerAdapter(activity as Context, restaurantList)

                            recyclerHome.adapter = recyclerAdapter

                            recyclerHome.layoutManager = layoutManager

                        }
                        else {
                            val dialog = AlertDialog.Builder(activity as Context)
                            dialog.setTitle("Failed")
                            dialog.setMessage("Could not load Restaurants")
                            dialog.setPositiveButton("Okay") {text, Listener -> }
                            dialog.create()
                            dialog.show()
                        }

                    } catch (e : JSONException) {
                        val dialog = AlertDialog.Builder(activity as Context)
                        dialog.setTitle("Failed")
                        dialog.setMessage("Some Error has occurred in JSON request!!!")
                        dialog.setPositiveButton("Okay") {text, Listener -> }
                        dialog.create()
                        dialog.show()
                    }
                },

                Response.ErrorListener{
                    val dialog = AlertDialog.Builder(activity as Context)
                    dialog.setTitle("Failed")
                    dialog.setMessage("Some Error has occurred in Volley!!!")
                    dialog.setPositiveButton("Okay") {text, Listener -> }
                    dialog.create()
                    dialog.show()
                }
        ) {
            override fun getHeaders(): MutableMap<String, String>
            {
                val headers = HashMap <String, String> ()
                headers ["Content-type"] = "application/json"
                headers ["token"] = "fb3de6b5669b46"
                return headers
            }
        }


        val online = ConnectionManager().checkConnectivity(activity as Context)

        if(online)
        {
            queueOfRequests.add(jsonObjReq)
        }
        else
        {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Offline")
            dialog.setMessage("Internet Connection is not available")
            dialog.setPositiveButton("Open Settings") {text, Listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
            }
            dialog.setNegativeButton("Close App") {text, Listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return homeView
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val id = item.itemId
        when(id)
        {
            R.id.alpha_sort -> {
                Collections.sort( restaurantList, alphaComp )
            }
            R.id.rating_sort -> {
                Collections.sort( restaurantList, ratingComp )
                restaurantList.reverse()
            }
        }
        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

    class BackendIsResPresent (val context:Context, val restaurant: Restaurant)
        : AsyncTask<Void, Void, Boolean>()
    {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java,
                "restaurants-db").build()

        override fun doInBackground(vararg params: Void?): Boolean
        {
            val res : RestaurantEntity? = db.resDao().getResById(restaurant.id)
            db.close()
            return res != null

        }

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}