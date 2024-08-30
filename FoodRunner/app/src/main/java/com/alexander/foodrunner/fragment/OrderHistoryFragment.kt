package com.alexander.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexander.foodrunner.R
import com.alexander.foodrunner.adapter.HomeRecyclerAdapter
import com.alexander.foodrunner.adapter.OrderHistoryRecyclerAdapter
import com.alexander.foodrunner.model.Food
import com.alexander.foodrunner.model.Order
import com.alexander.foodrunner.model.Restaurant
import com.alexander.foodrunner.model.RestaurantHolder
import com.alexander.foodrunner.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class OrderHistoryFragment : Fragment()
{
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerLayout: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    lateinit var loading: RelativeLayout

    val orderHistoryList = arrayListOf<Order>()

    var dateComp = Comparator<Order> { orderA, orderB ->
        orderA.orderTime.compareTo(orderB.orderTime, true) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {

        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerView = view.findViewById(R.id.recyclerOrderHistory)
        loading = view.findViewById(R.id.loadingHistory)
        recyclerLayout = LinearLayoutManager(activity as Context)
        val userFile: SharedPreferences?
                = activity?.getSharedPreferences(getString(R.string.logindata),Context.MODE_PRIVATE)


        setHasOptionsMenu(true)
        recyclerView.layoutManager = recyclerLayout
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context,
                (recyclerLayout as LinearLayoutManager).orientation))
        loading.visibility = View.VISIBLE


        val userId = userFile?.getString("Id","no_user_id")

        val queueOfRequests = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

        val jsonObjReq = object : JsonObjectRequest( Method.GET, url, null,

                Response.Listener{
                    try {
                        loading.visibility = View.GONE

                        val maindata = it.getJSONObject("data")
                        val success = maindata.getBoolean("success")
                        val data = maindata.getJSONArray("data")

                        if (success)
                        {
                            for(x in 0 until data.length())
                            {
                                val obj = data.getJSONObject(x)
                                val foodItemsList = arrayListOf<Food>()
                                val foods = obj.getJSONArray("food_items")

                                for(y in 0 until foods.length())
                                {
                                    val foodObj = foods.getJSONObject(y)
                                    val food = Food(
                                            foodObj.getString("food_item_id"),
                                            foodObj.getString("name"),
                                            foodObj.getString("cost"),
                                            "restaurant_id"
                                    )
                                    foodItemsList.add(food)
                                }

                                val order = Order(
                                        obj.getString("order_id"),
                                        obj.getString("restaurant_name"),
                                        obj.getString("total_cost"),
                                        obj.getString("order_placed_at"),
                                        foodItemsList
                                )
                                orderHistoryList.add(order)
                            }

                            recyclerAdapter = OrderHistoryRecyclerAdapter(activity as Context, orderHistoryList)
                            recyclerView.adapter = recyclerAdapter

                        }
                        else {
                            val dialog = AlertDialog.Builder(activity as Context)
                            dialog.setTitle("Failed")
                            dialog.setMessage("Could not load Order History")
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


        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        inflater.inflate(R.menu.menu_history, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val id = item.itemId
        when(id)
        {
            R.id.oldest -> {
                Collections.sort( orderHistoryList, dateComp )
            }
            R.id.newest -> {
                Collections.sort( orderHistoryList, dateComp )
                orderHistoryList.reverse()
            }
        }
        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}