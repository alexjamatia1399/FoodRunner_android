package com.alexander.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexander.foodrunner.R
import com.alexander.foodrunner.model.Food

class OrderHistoryFoodsAdapter (val context: Context, val foodsList: ArrayList<Food>)
    : RecyclerView.Adapter<OrderHistoryFoodsAdapter.OrderHistoryFoodsViewHolder>()
{
    class OrderHistoryFoodsViewHolder (view: View) : RecyclerView.ViewHolder(view)
    {
        val foodName: TextView = view.findViewById(R.id.txtFoodNameHistory)
        val foodPrice: TextView = view.findViewById(R.id.txtFoodPriceHistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryFoodsViewHolder
    {
        val foodView = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_history_foods,parent,false)
        return OrderHistoryFoodsViewHolder(foodView)
    }

    override fun getItemCount(): Int
    {
        return foodsList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryFoodsViewHolder, position: Int)
    {
        val food = foodsList[position]
        holder.foodName.text = food.name
        holder.foodPrice.text = food.perCost
    }
}







