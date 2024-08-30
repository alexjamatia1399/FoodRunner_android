package com.alexander.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexander.foodrunner.R
import com.alexander.foodrunner.model.Order

class OrderHistoryRecyclerAdapter (val context: Context, val orders: ArrayList<Order>)
    : RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>()
{
    lateinit var recyclerFoodsAdapter: OrderHistoryFoodsAdapter

    class OrderHistoryViewHolder (view: View) : RecyclerView.ViewHolder(view)
    {
        val resName: TextView = view.findViewById(R.id.txtResNameOH)
        val date: TextView = view.findViewById(R.id.txtDateOfOrder)
        val recyclerFoods: RecyclerView = view.findViewById(R.id.recyclerFoodsOH)
        val totalCost: TextView = view.findViewById(R.id.txtTotalCostOfOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder
    {
        val orderView = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_order_history,parent,false)
        return OrderHistoryViewHolder(orderView)
    }

    override fun getItemCount(): Int
    {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int)
    {
        val order = orders[position]
        holder.resName.text = order.resName
        holder.date.text = order.orderTime
        holder.totalCost.text = order.totalCost

        holder.recyclerFoods.adapter = OrderHistoryFoodsAdapter(context, order.foodList)
        holder.recyclerFoods.layoutManager = LinearLayoutManager(context)

    }
}











