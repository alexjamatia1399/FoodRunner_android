package com.alexander.foodrunner.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexander.foodrunner.R
import com.alexander.foodrunner.database.FoodEntity

class MyCartRecyclerAdapter (val context: Context, private val orderList: List<FoodEntity>)
    : RecyclerView.Adapter<MyCartRecyclerAdapter.MyCartViewHolder>()
{
    class MyCartViewHolder (view: View) : RecyclerView.ViewHolder(view)
    {
        val txtFoodName : TextView = view.findViewById(R.id.txtFoodNameCart)
        val txtFoodPrice : TextView = view.findViewById(R.id.txtFoodPriceCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder
    {
        val orderView = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_my_cart, parent,false)
        return MyCartViewHolder(orderView)
    }

    override fun getItemCount(): Int
    {
        return orderList.size
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int)
    {
        val item = orderList[position]
        holder.txtFoodName.text = item.name
        holder.txtFoodPrice.text = item.price
    }

}